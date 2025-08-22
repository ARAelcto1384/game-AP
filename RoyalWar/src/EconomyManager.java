public class EconomyManager {

    // قیمت خرید هر واحد از مارکت (طلا)
    public static int getBuyPrice(ResourceType type) throws InvalidTradeException {
        if (type == ResourceType.STONE) return 5;
        if (type == ResourceType.WOOD)  return 3;
        if (type == ResourceType.FOOD)  return 2;
        throw new InvalidTradeException("این منبع در مارکت خرید/فروش نمی‌شود.");
    }

    // قیمت فروش هر واحد به مارکت (طلا) — ساده: 50% قیمت خرید
    public static int getSellPrice(ResourceType type) throws InvalidTradeException {
        return getBuyPrice(type) / 2;
    }

    // مارکت فقط با این منابع کار می‌کند
    public static boolean isTradable(ResourceType type) {
        return type == ResourceType.STONE || type == ResourceType.WOOD || type == ResourceType.FOOD;
    }
}