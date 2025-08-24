public class EndgameManager {

    public static final int FLAG_TARGET = 100;

    public void checkAndEndIfNeeded(GameManager gm) {
        if (gm.getGameState() == GameState.ENDED) return;

        // 1) تصرف همه قلعه‌ها
        Player winnerByConquest = checkConquestWin(gm);
        if (winnerByConquest != null) {
            gm.endGame(winnerByConquest, "تصرف تمام قلعه‌ها");
            return;
        }

        // 2) پرچم‌ها
        Player winnerByFlags = checkFlagsWin(gm);
        if (winnerByFlags != null) {
            gm.endGame(winnerByFlags, "جمع‌آوری 100 پرچم");
        }
    }

    private Player checkConquestWin(GameManager gm) {
        // اگر همه قلعه‌ها مالک یک بازیکن باشند → برنده
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

    private Player checkFlagsWin(GameManager gm) {
        // هر بازیکن که به 100 پرچم برسد برنده است؛ اگر چند نفر، بیشترین پرچم، سپس امتیاز، سپس کمترین id
        Player candidate = null;
        int bestFlags = -1;
        int bestScore = -1;

        for (Player p : gm.getPlayers()) {
            int flags = totalFlagsOf(p, gm);
            if (flags >= FLAG_TARGET) {
                int score = p.getScore();
                if (candidate == null
                || flags > bestFlags
                        || (flags == bestFlags && score > bestScore)
                        || (flags == bestFlags && score == bestScore && p.getId() < candidate.getId())) {
                    candidate = p;
                    bestFlags = flags;
                    bestScore = score;
                }
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