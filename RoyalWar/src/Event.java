public abstract class Event {
    protected String name;
    protected String description;
    protected double productionFactor;

    public Event(String name, String description, double productionFactor) {
        this.name = name;
        this.description = description;
        this.productionFactor = productionFactor;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }

    public void applyTo(Castle c) {
        if (c == null) return;
        c.applyProductionFactor(productionFactor);
    }
}