import java.util.HashMap;
import java.util.Map;

public class Castle {
    private Player owner;
    private Position position;
    private int health = 100;
    private int maxHealth = 100;
    private ResourceBundle resources;

    private Barracks barracks;
    private Mine mine;
    private Farm farm;
    private LumberMill lumberMill;
    private DefensiveStructure defensiveStructure;
    private PopularityManager popularityManager;

    // بوست‌های فعال کارت‌های زمان‌دار
    private Map<ResourceType, TimedBoost> activeBoosts = new HashMap<>();

    public Castle(Player owner, Position position) {
        this.owner = owner;
        this.position = position;
        this.resources = new ResourceBundle();

        // منابع اولیه
        resources.add(ResourceType.GOLD, 100);
        resources.add(ResourceType.STONE, 50);
        resources.add(ResourceType.WOOD, 50);
        resources.add(ResourceType.FOOD, 50);
        resources.add(ResourceType.FLAG, 0);

        // ساختمان‌ها
        this.barracks = new Barracks(this);
        this.mine = new Mine(this);
        this.farm = new Farm(this);
        this.lumberMill = new LumberMill(this);
        this.defensiveStructure = new DefensiveStructure(this);

        // سیستم رضایت
        this.popularityManager = new PopularityManager(this);
    }

    // ==== مالک و موقعیت ====
    public Player getOwner() { return owner; }
    public void setOwner(Player newOwner) { this.owner = newOwner; }
    public Position getPosition() { return position; }

    // ==== ساختمان‌ها ====
    public Barracks getBarracks() { return barracks; }
    public Mine getMine() { return mine; }
    public Farm getFarm() { return farm; }
    public LumberMill getLumberMill() { return lumberMill; }
    public DefensiveStructure getDefensiveStructure() { return defensiveStructure; }

    // ==== منابع ====
    public ResourceBundle getResources() { return resources; }
    public int getFlagCount() { return resources.get(ResourceType.FLAG); }

    // ==== سلامت ====
    public int getHealth() { return health; }
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }
    public boolean isDestroyed() { return health <= 0; }
    public void repair(int amount) {
        health += amount;
        if (health > maxHealth) health = maxHealth;
    }

    // ==== رضایت ====
    public PopularityManager getPopularityManager() { return popularityManager; }

    // ==== تولید منابع ====
    public void produceResources() {
        popularityManager.updatePopularity();
        double factor = popularityManager.getProductionFactor();

        produceWithBoost(ResourceType.STONE, mine.getLevel() * 5, factor);
        produceWithBoost(ResourceType.WOOD, lumberMill.getLevel() * 5, factor);
        produceWithBoost(ResourceType.FOOD, farm.getLevel() * 5, factor);

        // کم‌کردن مدت بوست‌های فعال
        activeBoosts.entrySet().removeIf(e -> e.getValue().decreaseAndCheckExpired());
    }

    // کمک متد برای تولید با بوست
    private void produceWithBoost(ResourceType type, int baseAmount, double baseFactor) {
        double totalFactor = baseFactor;
        if (activeBoosts.containsKey(type)) {
            totalFactor *= activeBoosts.get(type).getMultiplier();
        }
        resources.add(type, (int)(baseAmount * totalFactor));
    }

    // بوست کارت زمان‌دار
    public void addTimedBoost(ResourceType type, double multiplier, int rounds) {
        activeBoosts.put(type, new TimedBoost(multiplier, rounds));
    }

    // ==== رویدادها ====
    public void applyProductionFactor(double factor) {
        popularityManager.updatePopularity();
        double baseFactor = popularityManager.getProductionFactor();
        double totalFactor = baseFactor * factor;

        resources.add(ResourceType.STONE, (int)(mine.getLevel() * 5 * totalFactor));
        resources.add(ResourceType.WOOD, (int)(lumberMill.getLevel() * 5 * totalFactor));
        resources.add(ResourceType.FOOD, (int)(farm.getLevel() * 5 * totalFactor));
    }

    public void applyFoodFactor(double factor) {
        popularityManager.updatePopularity();
        double baseFactor = popularityManager.getProductionFactor();
        double totalFactor = baseFactor * factor;

        resources.add(ResourceType.FOOD, (int)(farm.getLevel() * 5 * totalFactor));
    }

    // ==== پرچم ====
    public void addFlag() {
        resources.add(ResourceType.FLAG, 1);
    }

    // ==== کلاس داخلی برای بوست‌ها ====
    private static class TimedBoost {
        private double multiplier;
        private int roundsLeft;

        public TimedBoost(double multiplier, int rounds) {
            this.multiplier = multiplier;
            this.roundsLeft = rounds;
        }

        public double getMultiplier() { return multiplier; }

        // کاهش مدت و بررسی انقضا
        public boolean decreaseAndCheckExpired() {
            roundsLeft--;
            return roundsLeft <= 0;
        }
    }
}