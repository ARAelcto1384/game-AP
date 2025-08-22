public class MonsterAI implements RoundListener {

    private int attackIntervalRounds = 3; // هر 3 راند یک‌بار
    private int energy = 100;             // انرژی اولیه
    private int baseAttackPower = 10;     // قدرت پایه حمله

    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = energy; }

    public int getBaseAttackPower() { return baseAttackPower; }
    public void setBaseAttackPower(int baseAttackPower) { this.baseAttackPower = baseAttackPower; }

    public int getAttackIntervalRounds() { return attackIntervalRounds; }
    public void setAttackIntervalRounds(int attackIntervalRounds) { this.attackIntervalRounds = attackIntervalRounds; }

    @Override
    public void onRoundStart(int round, GameManager gm) {
        // کاهش شدت حمله متناسب با انرژی (به عنوان قلاب ساده)
        int effectivePower = (int) Math.max(1, baseAttackPower * (energy / 100.0));

        // اگر زمان حمله دوره‌ای فرا رسیده باشد، یک حمله زمان‌بندی‌شده ثبت می‌کنیم
        if (attackIntervalRounds > 0 && round % attackIntervalRounds == 0) {
            gm.onMonsterAttackScheduled(round, effectivePower);
        }

        // الگوی ساده برای افت انرژی در طول بازی
        if (energy > 0) {
            energy -= 1;
        }
    }
}