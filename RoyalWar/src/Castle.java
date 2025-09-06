import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

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

    private Map<ResourceType, TimedBoost> activeBoosts = new HashMap<>();

    public Castle(Player owner, Position position) {
        this.owner = owner;
        this.position = position;
        this.resources = new ResourceBundle();

        // Primary sources
        resources.add(ResourceType.GOLD, 100);
        resources.add(ResourceType.STONE, 50);
        resources.add(ResourceType.WOOD, 50);
        resources.add(ResourceType.FOOD, 50);
        resources.add(ResourceType.FLAG, 0);

        // Buildings
        this.barracks = new Barracks(this);
        this.mine = new Mine(this);
        this.farm = new Farm(this);
        this.lumberMill = new LumberMill(this);
        this.defensiveStructure = new DefensiveStructure(this);

        // Consent system
        this.popularityManager = new PopularityManager(this);
    }

    public Player getOwner() { return owner; }
    public void setOwner(Player newOwner) { this.owner = newOwner; }
    public Position getPosition() { return position; }
    public void setPosition(Position pos) { this.position = pos; }

    public int getHealth() { return health; }
    public void setHealth(int h) { this.health = h; }
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }
    public boolean isDestroyed() { return health <= 0; }
    public void repair(int amount) {
        health += amount;
        if (health > maxHealth) health = maxHealth;
    }

    public Barracks getBarracks() { return barracks; }
    public Mine getMine() { return mine; }
    public Farm getFarm() { return farm; }
    public LumberMill getLumberMill() { return lumberMill; }
    public DefensiveStructure getDefensiveStructure() { return defensiveStructure; }


    public ResourceBundle getResources() { return resources; }
    public int getFlagCount() { return resources.get(ResourceType.FLAG); }

    public PopularityManager getPopularityManager() { return popularityManager; }

    public void produceResources() {
        popularityManager.updatePopularity();
        double factor = popularityManager.getProductionFactor();

        produceWithBoost(ResourceType.STONE, mine.getLevel() * 5, factor);
        produceWithBoost(ResourceType.WOOD, lumberMill.getLevel() * 5, factor);
        produceWithBoost(ResourceType.FOOD, farm.getLevel() * 5, factor);

        List<ResourceType> expiredBoosts = new ArrayList<>();
        for (Map.Entry<ResourceType, TimedBoost> entry : activeBoosts.entrySet()) {
            if (entry.getValue().decreaseAndCheckExpired()) {
                expiredBoosts.add(entry.getKey());
            }
        }
        for (ResourceType type : expiredBoosts) {
            activeBoosts.remove(type);
        }
    }

    private void produceWithBoost(ResourceType type, int baseAmount, double baseFactor) {
        double totalFactor = baseFactor;
        if (activeBoosts.containsKey(type)) {
            totalFactor *= activeBoosts.get(type).getMultiplier();
        }
        resources.add(type, (int)(baseAmount * totalFactor));
    }

    public void addTimedBoost(ResourceType type, double multiplier, int rounds) {
        activeBoosts.put(type, new TimedBoost(multiplier, rounds));
    }

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

    public void addFlag() {
        resources.add(ResourceType.FLAG, 1);
    }

    private static class TimedBoost {
        private double multiplier;
        private int roundsLeft;

        public TimedBoost(double multiplier, int rounds) {
            this.multiplier = multiplier;
            this.roundsLeft = rounds;
        }

        public double getMultiplier() { return multiplier; }

        public boolean decreaseAndCheckExpired() {
            roundsLeft--;
            return roundsLeft <= 0;
        }
    }
}