public class TimedCard implements Card {
    private String name;
    private String description;
    private ResourceType resourceType;
    private double productionMultiplier;
    private int durationRounds;
    private boolean active;

    public TimedCard(String name, String description, ResourceType resourceType,
                     double multiplier, int durationRounds) {
        this.name = name;
        this.description = description;
        this.resourceType = resourceType;
        this.productionMultiplier = multiplier;
        this.durationRounds = durationRounds;
        this.active = false;
    }

    @Override
    public String getName() { return name; }

    @Override
    public String getDescription() { return description; }

    @Override
    public void applyEffect(Player player, GameManager gm) {
        Castle c = gm.getCastleOf(player);
        if (c != null) {
            c.addTimedBoost(resourceType, productionMultiplier, durationRounds);
            active = true;
        }
    }
    public boolean isActive() { return active; }
}