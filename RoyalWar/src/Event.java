public abstract class Event {
    protected String name;
    protected String description;
    protected double productionFactor; // ضریب تغییر

    public Event(String name, String description, double productionFactor) {
        this.name = name;
        this.description = description;
        this.productionFactor = productionFactor;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }

    // اعمال اثر بر یک قلعه
    public void applyTo(Castle c) {
        if (c == null) return;
        // پیش‌فرض: روی همه تولیدات اثر بگذارد
        c.applyProductionFactor(productionFactor);
    }
}