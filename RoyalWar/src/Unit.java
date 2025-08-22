public abstract class Unit {
    protected String name;
    protected int attackPower;
    protected int costGold;
    protected int health;

    public Unit(String name, int attackPower, int costGold, int health) {
        this.name = name;
        this.attackPower = attackPower;
        this.costGold = costGold;
        this.health = health;
    }

    public String getName() { return name; }
    public int getAttackPower() { return attackPower; }
    public int getCostGold() { return costGold; }
    public int getHealth() { return health; }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }

    public boolean isAlive() {
        return health > 0;
    }
}