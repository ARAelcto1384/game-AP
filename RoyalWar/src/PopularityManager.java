public class PopularityManager {

    private Castle castle; // هر PopularityManager به یک قلعه وابسته است
    private int popularity; // 0 تا 100

    public PopularityManager(Castle castle) {
        this.castle = castle;
        this.popularity = 50; // مقدار اولیه متوسط
    }

    public int getPopularity() {
        return popularity;
    }

    public void updatePopularity() {
        // امنیت = قدرت دفاع ساختمان دفاعی + 10 برای هر سطح پادگان
        int security = 0;
        if (castle.getDefensiveStructure() != null) {
            security += castle.getDefensiveStructure().getDefensePower();
        }
        if (castle.getBarracks() != null) {
            security += (castle.getBarracks().getLevel() * 10);
        }

        // غذا = مقدار غذای ذخیره‌شده (سقف تأثیرگذاری: 100)
        int foodSupply = Math.min(castle.getResources().get(ResourceType.FOOD), 100);

        // فرمول ساده: میانگین امنیت و غذا، نرمال‌شده به بازه 0-100
        popularity = (security + foodSupply) / 2;

        if (popularity > 100) popularity = 100;
        if (popularity < 0) popularity = 0;
    }

    // ضریب اثرگذاری بر تولید: بین 0.5 و 1.5
    public double getProductionFactor() {
        if (popularity >= 80) return 1.5;
        if (popularity >= 60) return 1.2;
        if (popularity >= 40) return 1.0;
        if (popularity >= 20) return 0.8;
        return 0.5;
    }
}