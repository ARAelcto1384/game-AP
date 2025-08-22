import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BattleManager implements RoundListener {

    private List<Attack> scheduled = new ArrayList<>();

    public void schedule(Attack attack) {
        if (attack != null) scheduled.add(attack);
    }

    @Override
    public void onRoundStart(int round, GameManager gm) {
        // همه نبردهایی که نوبت اعلام نتیجه‌شان همین راند است را حل کن
        Iterator<Attack> it = scheduled.iterator();
        while (it.hasNext()) {
            Attack a = it.next();
            if (a.getResolutionRound() == round) {
                resolve(a, gm);
                it.remove();
            }
        }
    }

    private void resolve(Attack a, GameManager gm) {
        if (a.isMonsterAsAttacker()) {
            resolveMonsterAgainstCastle(a, gm);
            return;
        }

        if (a.getType() == AttackType.MONSTER) {
            resolvePlayerVsMonster(a, gm);
            return;
        }

        // RAID یا CONQUER علیه قلعه
        resolvePlayerAgainstCastle(a, gm);
    }

    private void resolveMonsterAgainstCastle(Attack a, GameManager gm) {
        Castle def = a.getTargetCastle();
        int attackPower = a.getFixedAttackPower();
        int defense = defensePowerOfCastle(def);

        int damage = Math.max(0, attackPower - defense);
        if (damage > 0) {
            def.takeDamage(damage);
        }
        // اگر سلامت به صفر رسید، از نظر سناریو تصرف می‌شود؛ اما هیولا مالک نمی‌شود.
        // بنابراین فقط قلعه تخریب‌شده و نیاز به تعمیر خواهد داشت.
    }

    private void resolvePlayerVsMonster(Attack a, GameManager gm) {
        int attackPower = totalAttackPower(a.getUnitsUsed());
        int defense = GameConfig.MONSTER_DEFENSE_BASE;

        boolean success = attackPower > defense;

        // بازگرداندن نیروها در صورت موفقیت، و از دست‌دادن کامل در صورت شکست
        Barracks atkBarracks = gm.getBarracksOf(a.getAttacker());
        if (success) {
            if (atkBarracks != null) atkBarracks.addUnits(a.getUnitsUsed());
            a.getAttacker().addScore(GameConfig.MONSTER_KILL_SCORE);
        } else {
            // نیروها از بین می‌روند (بازگردانی صورت نمی‌گیرد)
        }
    }

    private void resolvePlayerAgainstCastle(Attack a, GameManager gm) {
        Castle def = a.getTargetCastle();
        Barracks atkBarracks = gm.getBarracksOf(a.getAttacker());

        int attackPower = totalAttackPower(a.getUnitsUsed());
        int defense = defensePowerOfCastle(def);

        if (a.getType() == AttackType.RAID) {
            boolean success = attackPower > defense;
            if (success) {
                stealResources(def, gm.getCastleOf(a.getAttacker()));
                // همه نیروها سالم برمی‌گردند
                if (atkBarracks != null) atkBarracks.addUnits(a.getUnitsUsed());
            } else {
                // شکست: همه نیروها از بین می‌روند
            }
        } else if (a.getType() == AttackType.CONQUER) {
            int damage = Math.max(0, attackPower - defense);
            if (damage > 0) {
                def.takeDamage(damage);
            }

            if (def.isDestroyed()) {
                // انتقال مالکیت قلعه به مهاجم
                gm.changeCastleOwner(def, a.getAttacker());
                // بازیابی بخشی از سلامت پس از تصرف
                def.repair(GameConfig.CAPTURE_RESTORE_HEALTH);
                // نیروها برمی‌گردند
                if (atkBarracks != null) atkBarracks.addUnits(a.getUnitsUsed());
            } else {
                // هنوز تصرف نشده؛ نیروها از بین می‌روند (قانون ساده برای ترم دومی)
            }
        }
    }

    private int totalAttackPower(List<Unit> units) {
        int sum = 0;
        for (Unit u : units) sum += u.getAttackPower();
        return sum;
    }

    private int defensePowerOfCastle(Castle c) {
        int def = 0;
        if (c.getDefensiveStructure() != null) {
            def += c.getDefensiveStructure().getDefensePower();
        }
        if (c.getBarracks() != null) {
            def += c.getBarracks().totalAttackPower(); // همه نیروهای مستقر مدافع‌اند
        }
        return def;
    }

    private void stealResources(Castle from, Castle to) {
        int percent = GameConfig.RAID_STEAL_PERCENT;
        if (from == null || to == null) return;

        for (ResourceType rt : new ResourceType[]{ResourceType.STONE, ResourceType.WOOD, ResourceType.FOOD}) {
            int have = from.getResources().get(rt);
            int steal = (have * percent) / 100;
            if (steal > 0) {
                from.getResources().consume(rt, steal);
                to.getResources().add(rt, steal);
            }
        }
        // غنیمت طلا (اختیاری): برای سادگی فعلاً انجام نمی‌شود.
    }
}