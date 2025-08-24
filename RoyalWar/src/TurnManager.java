import java.util.ArrayList;
import java.util.List;

public class TurnManager {
    private int currentRound = 1;
    private int currentIndex = 0;     // اندیس بازیکن جاری در players
    private int playersCount = GameConfig.MIN_PLAYERS;

    private List<RoundListener> listeners = new ArrayList<>();

    public void configurePlayersCount(int count) {
        if (count < GameConfig.MIN_PLAYERS || count > GameConfig.MAX_PLAYERS) {
            throw new IllegalArgumentException("تعداد بازیکنان باید بین 2 تا 4 باشد.");
        }
        this.playersCount = count;
    }

    public int getCurrentRound() { return currentRound; }
    public int getCurrentIndex() { return currentIndex; }
    public void setCurrentRound(int round) { this.currentRound = round; }
    public void setCurrentIndex(int index) { this.currentIndex = index; }

    public void addListener(RoundListener l) {
        if (l != null) listeners.add(l);
    }

    public void startFirstRound(GameManager gm) {
        notifyRoundStart(gm);
        gm.getCurrentPlayer().resetActionForTurn();
    }

    public void endTurn(GameManager gm) {
        currentIndex++;
        if (currentIndex >= playersCount) {
            currentIndex = 0;
            currentRound++;
            notifyRoundStart(gm);
        }
        gm.getCurrentPlayer().resetActionForTurn();
    }

    private void notifyRoundStart(GameManager gm) {
        for (RoundListener l : listeners) {
            l.onRoundStart(currentRound, gm);
        }
    }
}