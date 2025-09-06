import java.util.ArrayList;
import java.util.List;

public class CardManager {
    private List<Card> availableCards;
    private List<Card> usedCards;

    public CardManager() {
        this.availableCards = new ArrayList<>();
        this.usedCards = new ArrayList<>();

        // 9 time cards
        availableCards.add(new TimedCard("Stone +20%", "increase 20٪ produce stone.", ResourceType.STONE, 1.2, 3));
        availableCards.add(new TimedCard("Stone +50%", "increase 50٪ produce stone.", ResourceType.STONE, 1.5, 3));
        availableCards.add(new TimedCard("Stone +100%", "increase 100٪ produce stone.", ResourceType.STONE, 2.0, 2));
        availableCards.add(new TimedCard("Wood +20%", "increase 20٪ produce wood.", ResourceType.WOOD, 1.2, 3));
        availableCards.add(new TimedCard("Wood +50%", "increase 50٪ produce wood.", ResourceType.WOOD, 1.5, 3));
        availableCards.add(new TimedCard("Wood +100%", "increase 100٪ produce wood.", ResourceType.WOOD, 2.0, 2));
        availableCards.add(new TimedCard("Food +20%", "increase 20٪ produce food.", ResourceType.FOOD, 1.2, 3));
        availableCards.add(new TimedCard("Food +50%", "increase 50٪ produce food.", ResourceType.FOOD, 1.5, 3));
        availableCards.add(new TimedCard("Food +100%", "increase 100٪ produce food.", ResourceType.FOOD, 2.0, 2));

        // 2 quick cards
        availableCards.add(new InstantCard("Immediate resources ", "Immediate increase 1000 units of stone, wood and food.", ResourceType.STONE, 1000));
        availableCards.add(new InstantCard("Immediate gold", "Immediate increase 10 kilos of gold.", ResourceType.GOLD, 10));
    }

    public List<Card> getAvailableCards() { return availableCards; }

    public void useCard(Card card, Player player, GameManager gm)
            throws CardNotAvailableException, CardAlreadyUsedException {
        if (!availableCards.contains(card)) {
            throw new CardNotAvailableException("This card is not available!");
        }
        if (usedCards.contains(card)) {
            throw new CardAlreadyUsedException("This card has already been used!");
        }
        card.applyEffect(player, gm);
        usedCards.add(card);
    }
}