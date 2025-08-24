public class RainEvent extends Event {
    public RainEvent() {
        super("باران", "افزایش ۲۰٪ تولید غذا به دلیل باران", 1.2);
    }

    @Override
    public void applyTo(Castle c) {
        if (c == null) return;
        c.applyFoodFactor(productionFactor);
    }
}