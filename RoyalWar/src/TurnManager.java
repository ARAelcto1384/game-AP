import java.util.ArrayList;
import java.util.List;

public class TurnManager {
    private int currentRound = 1;
    private int currentPlayerId = 1; // 1 یا 2

    private List<RoundListener> listeners = new ArrayList<>();

    public int getCurrentRound() { return currentRound; }
    public int getCurrentPlayerId() { return currentPlayerId; }

    public void addListener(RoundListener l) {
        if (l != null) listeners.add(l);
    }

    public boolean isPlayersTurn(Player p) {
        return p != null && p.getId() == currentPlayerId;
    }

    // شروع اولیه نوبت‌ها: راند 1 و بازیکن 1
    public void startFirstRound(GameManager gm) {
        notifyRoundStart(gm);
        gm.getPlayer1().resetActionForTurn();
    }

    public void startTurnForCurrentPlayer(GameManager gm) {
        Player p = (currentPlayerId == 1) ? gm.getPlayer1() : gm.getPlayer2();
        p.resetActionForTurn();
    }

    public void endTurn(GameManager gm) {
        if (currentPlayerId == 1) {
            currentPlayerId = 2;
            startTurnForCurrentPlayer(gm);
        } else {
            currentPlayerId = 1;
            currentRound++;
            notifyRoundStart(gm);
            startTurnForCurrentPlayer(gm);
        }
    }

    private void notifyRoundStart(GameManager gm) {
        for (RoundListener l : listeners) {
            l.onRoundStart(currentRound, gm);
        }
    }
}