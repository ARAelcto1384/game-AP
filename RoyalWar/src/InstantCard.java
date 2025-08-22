public class InstantCard implements Card {
    private String name;
    private String description;
    private ResourceType resourceType;
    private int amount;

    public InstantCard(String name, String description, ResourceType resourceType, int amount) {
        this.name = name;
        this.description = description;
        this.resourceType = resourceType;
        this.amount = amount;
    }

    @Override
    public String getName() { return name; }

    @Override
    public String getDescription() { return description; }

    @Override
    public void applyEffect(Player player, GameManager gm) {
        Castle c = gm.getCastleOf(player);
        if (c != null) {
            c.getResources().add(resourceType, amount);
        }
    }
}