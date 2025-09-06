public class RainEvent extends Event {
    public RainEvent() {
        super("Rain", "20% increase in food production due to rain", 1.2);
    }
    @Override
    public void applyTo(Castle c) {
        if (c == null) return;
        c.applyFoodFactor(productionFactor);
    }
}