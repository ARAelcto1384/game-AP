import java.util.ArrayList;
import java.util.List;

public class Attack {
    private AttackType type;
    private Player attacker;        // ممکن است در حمله هیولا null باشد
    private Castle targetCastle;    // برای RAID/CONQUER الزامی است
    private boolean monsterAsAttacker;  // اگر true یعنی حمله از طرف هیولا به قلعه
    private int fixedAttackPower;       // برای حمله هیولا یا سناریوهایی بدون لیست نیرو
    private List<Unit> unitsUsed;       // نیروهای مصرف‌شده برای حمله بازیکن
    private int resolutionRound;        // راند اعلام نتیجه (راند بعدی)

    public Attack(AttackType type, Player attacker, Castle targetCastle, int resolutionRound) {
        this.type = type;
        this.attacker = attacker;
        this.targetCastle = targetCastle;
        this.unitsUsed = new ArrayList<>();
        this.resolutionRound = resolutionRound;
        this.monsterAsAttacker = false;
        this.fixedAttackPower = 0;
    }

    // سازنده حمله هیولا به قلعه
    public static Attack monsterAttack(Castle targetCastle, int effectivePower, int resolutionRound) {
        Attack a = new Attack(AttackType.CONQUER, null, targetCastle, resolutionRound);
        a.monsterAsAttacker = true;
        a.fixedAttackPower = effectivePower;
        return a;
    }

    public AttackType getType() { return type; }
    public Player getAttacker() { return attacker; }
    public Castle getTargetCastle() { return targetCastle; }
    public boolean isMonsterAsAttacker() { return monsterAsAttacker; }
    public int getFixedAttackPower() { return fixedAttackPower; }
    public List<Unit> getUnitsUsed() { return unitsUsed; }
    public int getResolutionRound() { return resolutionRound; }

    public void addUnits(List<Unit> units) {
        if (units == null) return;
        unitsUsed.addAll(units);
    }
}