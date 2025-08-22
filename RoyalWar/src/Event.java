public abstract class Event {
    protected String name;
    protected String description;
    protected double productionFactor; // ضریب تغییر در تولید منابع

    public Event(String name, String description, double productionFactor) {
        this.name = name;
        this.description = description;
        this.productionFactor = productionFactor;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }

    // اثرگذاری روی هر دو قلعه
    public void applyEffect(Castle c1, Castle c2) {
        if (c1 != null) c1.applyProductionFactor(productionFactor);
        if (c2 != null) c2.applyProductionFactor(productionFactor);
    }
}