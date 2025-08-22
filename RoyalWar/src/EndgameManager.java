public class EndgameManager {

    public static final int FLAG_TARGET = 100;

    public void checkAndEndIfNeeded(GameManager gm) {
        if (gm.getGameState() == GameState.ENDED) return;

        Player p1 = gm.getPlayer1();
        Player p2 = gm.getPlayer2();

        boolean p1OwnsAll = ownsAllCastles(p1, gm);
        boolean p2OwnsAll = ownsAllCastles(p2, gm);

        int p1Flags = totalFlagsOf(p1, gm);
        int p2Flags = totalFlagsOf(p2, gm);

        // برد با تصرف همه قلعه‌ها
        if (p1OwnsAll && !p2OwnsAll) {
            gm.endGame(p1, "تصرف تمام قلعه‌ها");
            return;
        }
        if (p2OwnsAll && !p1OwnsAll) {
            gm.endGame(p2, "تصرف تمام قلعه‌ها");
            return;
        }

        // برد با پرچم‌ها
        boolean p1FlagWin = p1Flags >= FLAG_TARGET;
        boolean p2FlagWin = p2Flags >= FLAG_TARGET;

        if (p1FlagWin && !p2FlagWin) {
            gm.endGame(p1, "جمع‌آوری 100 پرچم");
            return;
        }
        if (p2FlagWin && !p1FlagWin) {
            gm.endGame(p2, "جمع‌آوری 100 پرچم");
            return;
        }

        // همزمان هر دو به حد پرچم رسیده‌اند → انتخاب ساده: پرچم بیشتر، سپس امتیاز
        if (p1FlagWin && p2FlagWin) {
            if (p1Flags > p2Flags) {
                gm.endGame(p1, "جمع‌آوری 100 پرچم (برتری تعداد)");
            } else if (p2Flags > p1Flags) {
                gm.endGame(p2, "جمع‌آوری 100 پرچم (برتری تعداد)");
            } else {
                // تساوی پرچم → امتیاز بازیکن‌ها (در صورت برابری کامل، پیش‌فرض Player1)
                if (p1.getScore() > p2.getScore()) {
                    gm.endGame(p1, "جمع‌آوری 100 پرچم (برتری امتیاز)");
                } else if (p2.getScore() > p1.getScore()) {
                    gm.endGame(p2, "جمع‌آوری 100 پرچم (برتری امتیاز)");
                } else {
                    gm.endGame(p1, "جمع‌آوری 100 پرچم (تساوی کامل، اولویت با Player1)");
                }
            }
        }
    }

    private boolean ownsAllCastles(Player p, GameManager gm) {
        // در اسکلت فعلی 2 قلعه داریم؛ مالکیت هر دو باید با p باشد
        return gm.getCastle1().getOwner().getId() == p.getId()
                && gm.getCastle2().getOwner().getId() == p.getId();
    }

    private int totalFlagsOf(Player p, GameManager gm) {
        int sum = 0;
        if (gm.getCastle1().getOwner().getId() == p.getId()) {
            sum += gm.getCastle1().getFlagCount();
        }
        if (gm.getCastle2().getOwner().getId() == p.getId()) {
            sum += gm.getCastle2().getFlagCount();
        }
        return sum;
    }
}