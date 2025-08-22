public class RainEvent extends Event {
    public RainEvent() {
        super("باران", "افزایش ۲۰٪ تولید غذا به دلیل باران", 1.2);
    }

    @Override
    public void applyEffect(Castle c1, Castle c2) {
        if (c1 != null) c1.applyFoodFactor(productionFactor);
        if (c2 != null) c2.applyFoodFactor(productionFactor);
    }
}