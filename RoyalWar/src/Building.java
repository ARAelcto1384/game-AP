public abstract class Building implements Upgradable {
    protected String name;
    protected int level;
    protected int maxLevel = 3;
    protected int baseUpgradeCostGold; // هزینه ارتقاء برای هر سطح
    protected Castle owner;

    public Building(String name, int baseUpgradeCostGold, Castle owner) {
        this.name = name;
        this.level = 1;
        this.baseUpgradeCostGold = baseUpgradeCostGold;
        this.owner = owner;
    }

    public String getName() { return name; }
    public int getLevel() { return level; }

    @Override
    public void upgrade() throws NotEnoughGoldException, MaxLevelReachedException {
        if (level >= maxLevel) {
            throw new MaxLevelReachedException(name + " در بیشترین سطح است.");
        }
        int cost = baseUpgradeCostGold * level; // هزینه ارتقا با سطح فعلی
        if (!owner.getResources().consume(ResourceType.GOLD, cost)) {
            throw new NotEnoughGoldException("طلای کافی برای ارتقاء " + name + " وجود ندارد.");
        }
        level++;
        onUpgrade();
    }

    // رفتار خاص هر ساختمان هنگام ارتقاء
    protected abstract void onUpgrade();
}