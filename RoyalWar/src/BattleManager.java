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

        // 🎯 Hook شروع نبرد
        for (GameEventListener l : gm.getUIListeners()) {
            l.onBattleStarted(a);
        }

        // 📜 لاگ شروع نبرد (ایمن در برابر null)
        if (a.getAttacker() != null) {
            gm.getLogger().log("حمله آغاز شد: " + a.getType() +
                    " توسط " + a.getAttacker().getName());
        } else {
            gm.getLogger().log("حمله هیولا به قلعه " +
                    a.getTargetCastle().getOwner().getName());
        }

        // --- منطق نبرد ---
        if (a.isMonsterAsAttacker()) {
            resolveMonsterAgainstCastle(a, gm);
        }
        else if (a.getType() == AttackType.MONSTER) {
            resolvePlayerVsMonster(a, gm);
        }
        else {
            resolvePlayerAgainstCastle(a, gm);
        }

        // 🎯 Hook پایان نبرد
        for (GameEventListener l : gm.getUIListeners()) {
            l.onBattleResolved(a);
        }

        // 📜 لاگ پایان نبرد
        gm.getLogger().log("نبرد پایان یافت: " + a.getType());
    }

    private void resolveMonsterAgainstCastle(Attack a, GameManager gm) {
        Castle def = a.getTargetCastle();
        int attackPower = a.getFixedAttackPower();
        int defense = defensePowerOfCastle(def);

        int damage = Math.max(0, attackPower - defense);
        if (damage > 0) def.takeDamage(damage);
    }

    private void resolvePlayerVsMonster(Attack a, GameManager gm) {
        int attackPower = totalAttackPower(a.getUnitsUsed());
        int defense = GameConfig.MONSTER_DEFENSE_BASE;
        boolean success = attackPower > defense;

        Barracks atkBarracks = gm.getBarracksOf(a.getAttacker());
        if (success) {
            if (atkBarracks != null) atkBarracks.addUnits(a.getUnitsUsed());
            a.getAttacker().addScore(GameConfig.MONSTER_KILL_SCORE);
        }
    }

    private void resolvePlayerAgainstCastle(Attack a, GameManager gm) {
        Castle def = a.getTargetCastle();
        Barracks atkBarracks = gm.getBarracksOf(a.getAttacker());

        int attackPower = totalAttackPower(a.getUnitsUsed());
        int defense = defensePowerOfCastle(def);

        if (a.getType() == AttackType.RAID) {
            if (attackPower > defense) {
                stealResources(def, gm.getCastleOf(a.getAttacker()));
                if (atkBarracks != null) atkBarracks.addUnits(a.getUnitsUsed());
            }
        }
        else if (a.getType() == AttackType.CONQUER) {
            int damage = Math.max(0, attackPower - defense);
            if (damage > 0) def.takeDamage(damage);
            if (def.isDestroyed()) {
                gm.changeCastleOwner(def, a.getAttacker());
                def.repair(GameConfig.CAPTURE_RESTORE_HEALTH);
                if (atkBarracks != null) atkBarracks.addUnits(a.getUnitsUsed());
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
        if (c.getDefensiveStructure() != null)
            def += c.getDefensiveStructure().getDefensePower();
        if (c.getBarracks() != null)
            def += c.getBarracks().totalAttackPower();
        return def;
    }

    private void stealResources(Castle from, Castle to) {
        int percent = GameConfig.RAID_STEAL_PERCENT;
        for (ResourceType rt : new ResourceType[]{ResourceType.STONE, ResourceType.WOOD, ResourceType.FOOD}) {
            int have = from.getResources().get(rt);
            int steal = (have * percent) / 100;
            if (steal > 0) {
                from.getResources().consume(rt, steal);
                to.getResources().add(rt, steal);
            }
        }
    }
}