public class PopularityManager {
    private Castle castle;
    private int popularity;

    public PopularityManager(Castle castle) {
        this.castle = castle;
        this.popularity = 50;
    }

    public int getPopularity() {
        return popularity;
    }

    public void updatePopularity() {
        // Security = Defensive building defense strength + 10 for each level of barracks
        int security = 0;
        if (castle.getDefensiveStructure() != null) {
            security += castle.getDefensiveStructure().getDefensePower();
        }
        if (castle.getBarracks() != null) {
            security += (castle.getBarracks().getLevel() * 10);
        }

        // Food = amount of food stored (cap: 100)
        int foodSupply = Math.min(castle.getResources().get(ResourceType.FOOD), 100);

        // Average security and food
        popularity = (security + foodSupply) / 2;
        if (popularity > 100) popularity = 100;
        if (popularity < 0) popularity = 0;
    }

    //Effect popularity
    public double getProductionFactor() {
        if (popularity >= 80) return 1.5;
        if (popularity >= 60) return 1.2;
        if (popularity >= 40) return 1.0;
        if (popularity >= 20) return 0.8;
        return 0.5;
    }
}