public interface Card {
    String getName();
    String getDescription();
    void applyEffect(Player player, GameManager gm);
}