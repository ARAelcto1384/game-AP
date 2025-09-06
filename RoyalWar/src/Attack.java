import java.util.ArrayList;
import java.util.List;

public class Attack {
    private AttackType type;
    private Player attacker;
    private Castle targetCastle;
    private boolean monsterAsAttacker;
    private int fixedAttackPower;
    private List<Unit> unitsUsed;
    private int resolutionRound;

    public Attack(AttackType type, Player attacker, Castle targetCastle, int resolutionRound) {
        this.type = type;
        this.attacker = attacker;
        this.targetCastle = targetCastle;
        this.unitsUsed = new ArrayList<>();
        this.resolutionRound = resolutionRound;
        this.monsterAsAttacker = false;
        this.fixedAttackPower = 0;
    }

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