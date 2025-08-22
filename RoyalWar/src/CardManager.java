import java.util.ArrayList;
import java.util.List;

public class CardManager {
    private List<Card> availableCards;
    private List<Card> usedCards;

    public CardManager() {
        this.availableCards = new ArrayList<>();
        this.usedCards = new ArrayList<>();

        // 9 کارت زمان‌دار (سنگ، چوب، غذا × سه شدت متفاوت)
        availableCards.add(new TimedCard("سنگ +20%", "افزایش 20٪ تولید سنگ", ResourceType.STONE, 1.2, 3));
        availableCards.add(new TimedCard("سنگ +50%", "افزایش 50٪ تولید سنگ", ResourceType.STONE, 1.5, 3));
        availableCards.add(new TimedCard("سنگ +100%", "افزایش 100٪ تولید سنگ", ResourceType.STONE, 2.0, 2));

        availableCards.add(new TimedCard("چوب +20%", "افزایش 20٪ تولید چوب", ResourceType.WOOD, 1.2, 3));
        availableCards.add(new TimedCard("چوب +50%", "افزایش 50٪ تولید چوب", ResourceType.WOOD, 1.5, 3));
        availableCards.add(new TimedCard("چوب +100%", "افزایش 100٪ تولید چوب", ResourceType.WOOD, 2.0, 2));

        availableCards.add(new TimedCard("غذا +20%", "افزایش 20٪ تولید غذا", ResourceType.FOOD, 1.2, 3));
        availableCards.add(new TimedCard("غذا +50%", "افزایش 50٪ تولید غذا", ResourceType.FOOD, 1.5, 3));
        availableCards.add(new TimedCard("غذا +100%", "افزایش 100٪ تولید غذا", ResourceType.FOOD, 2.0, 2));

        // 2 کارت فوری (یکی انتخاب می‌شود)
        availableCards.add(new InstantCard("منابع فوری", "افزایش فوری 1000 واحد سنگ، چوب و غذا", ResourceType.STONE, 1000));
        availableCards.add(new InstantCard("طلای فوری", "افزایش فوری 10 کیلو طلا", ResourceType.GOLD, 10));
    }

    public List<Card> getAvailableCards() { return availableCards; }

    public void useCard(Card card, Player player, GameManager gm)
            throws CardNotAvailableException, CardAlreadyUsedException {
        if (!availableCards.contains(card)) {
            throw new CardNotAvailableException("این کارت در دسترس نیست.");
        }
        if (usedCards.contains(card)) {
            throw new CardAlreadyUsedException("این کارت قبلاً استفاده شده است.");
        }
        card.applyEffect(player, gm);
        usedCards.add(card);
    }
}