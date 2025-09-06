public class EndgameManager {

    public static final int FLAG_TARGET = 100;

    public void checkAndEndIfNeeded(GameManager gm) {
        if (gm.getGameState() == GameState.ENDED) return;

        Player winnerByConquest = checkConquestWin(gm);
        if (winnerByConquest != null) {
            gm.endGame(winnerByConquest, "Capture all the castles");
            return;
        }

        Player winnerByFlags = checkFlagsWin(gm);
        if (winnerByFlags != null) {
            gm.endGame(winnerByFlags, "Collecting 100 flags");
        }
    }

    private Player checkConquestWin(GameManager gm) {
        // If the player owns all the castles, he wins.
        for (Player p : gm.getPlayers()) {
            boolean ownsAll = true;
            for (Castle c : gm.getCastles()) {
                if (c.getOwner().getId() != p.getId()) {
                    ownsAll = false; break;
                }
            }
            if (ownsAll) return p;
        }
        return null;
    }

    // Any player who reaches 100 flags wins.
    private Player checkFlagsWin(GameManager gm) {

        Player candidate = null;

        for (Player p : gm.getPlayers()) {
            int flags = totalFlagsOf(p, gm);
            if (flags >= FLAG_TARGET) {
                candidate = p;
            }
        }
        return candidate;
    }

    private int totalFlagsOf(Player p, GameManager gm) {
        int sum = 0;
        for (Castle c : gm.getCastles()) {
            if (c.getOwner().getId() == p.getId()) {
                sum += c.getFlagCount();
            }
        }
        return sum;
    }
}