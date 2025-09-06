public class EconomyManager {

    // Purchase price per unit from the market (gold)
    public static int getBuyPrice(ResourceType type) throws InvalidTradeException {
        if (type == ResourceType.STONE) return 5;
        if (type == ResourceType.WOOD)  return 3;
        if (type == ResourceType.FOOD)  return 2;
        throw new InvalidTradeException("This resource cannot be bought/sold on the market!");
    }

    // Selling price per unit to the market (gold) — simple: 50% of the purchase price
    public static int getSellPrice(ResourceType type) throws InvalidTradeException {
        return getBuyPrice(type) / 2;
    }
    
    public static boolean isTradable(ResourceType type) {
        switch (type) {
            case STONE:
            case WOOD:
            case FOOD:
                return true;
            default:
                return false;
        }
    }
}