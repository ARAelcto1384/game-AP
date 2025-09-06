public abstract class Building implements Upgradable {
    protected String name;
    protected int level;
    protected int maxLevel = 3;
    protected int baseUpgradeCostGold;
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
            throw new MaxLevelReachedException(name + " is at the highest level!");
        }
        int cost = baseUpgradeCostGold * level;
        if (!owner.getResources().consume(ResourceType.GOLD, cost)) {
            throw new NotEnoughGoldException("There is not enough gold to upgrade " + name + " !");
        }
        level++;
        onUpgrade();
    }

    // Specific behavior of each building when upgrading
    protected abstract void onUpgrade();
}