import java.util.ArrayList;
import java.util.List;

public class FullGameTestRunner {

    public static void main(String[] args) throws Exception {
        int[] playerCounts = {2, 3, 4};
        for (int count : playerCounts) {
            System.out.println("\n============================");
            System.out.println("شروع تست سناریوی " + count + " بازیکن");
            boolean success = runScenario(count);
            System.out.println("نتیجه تست " + count + " بازیکن: "
                    + (success ? "✅ موفق" : "❌ مشکل دارد"));
            System.out.println("============================\n");
        }
    }

    private static boolean runScenario(int count) {
        try {
            // 1) ساخت نام‌ها
            List<String> names = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                names.add("Player" + i);
            }

            // 2) ساخت GameManager
            GameManager gm = new GameManager(names);

            // 3) اضافه کردن Hook تست
            gm.addUIListener(new GameEventListener() {
                @Override
                public void onPlayerMoved(Player player, Position newPos) {
                    System.out.println("[HOOK] حرکت " + player.getName() + " به " + newPos);
                }
                @Override
                public void onTurnEnded(Player prev, int nextId, int round) {
                    System.out.println("[HOOK] پایان نوبت " + prev.getName() + " → شروع نوبت بازیکن " + nextId);
                }
                @Override
                public void onCastleCaptured(Castle castle, Player newOwner) {
                    System.out.println("[HOOK] قلعه تصرف شد توسط " + newOwner.getName());
                }
                @Override
                public void onEventTriggered(Event event) {
                    System.out.println("[HOOK] رویداد: " + event.getName());
                }
                @Override
                public void onBattleStarted(Attack attack) {
                    System.out.println("[HOOK] نبرد آغاز شد: " + attack.getType());
                }
                @Override
                public void onBattleResolved(Attack attack) {
                    System.out.println("[HOOK] نبرد پایان یافت");
                }
                @Override
                public void onGameEnded(Player winner, String reason) {
                    System.out.println("[HOOK] بازی پایان یافت → برنده: " + winner.getName());
                }
            });

            // 4) اجرای چند حرکت برای هر بازیکن
            for (int i = 0; i < count; i++) {
                try {
                    gm.moveCurrentPlayer(Direction.UP);
                } catch (Exception e) {
                    System.out.println("⚠️ حرکت انجام نشد: " + e.getMessage());
                }
                gm.endTurn();
            }

            // 5) ذخیره بازی
            SaveLoadManager.save(gm);
            System.out.println("💾 وضعیت ذخیره شد.");

            // 6) لود مجدد
            GameManager loaded = SaveLoadManager.load();
            System.out.println("📂 وضعیت لود شد. نوبت فعلی: " + loaded.getCurrentPlayer().getName());

            // 7) حمله آزمایشی به بازیکن بعدی
            try {
                int targetId = loaded.getPlayers()
                        .get((loaded.getTurnManager().getCurrentIndex() + 1) % loaded.getPlayers().size())
                        .getId();
                loaded.initiateAttackOnCastle(targetId, AttackType.RAID, 1, 0, 0, 0, 0);
                System.out.println("⚔️ حمله تستی به بازیکن " + targetId + " برنامه‌ریزی شد.");
            } catch (Exception e) {
                System.out.println("⚠️ حمله تستی اجرا نشد: " + e.getMessage());
            }

            // 8) نمایش خلاصه لاگ
            System.out.println("📜 خلاصه لاگ (" + loaded.getLogger().getEntries().size() + " رویداد):");
            for (GameLogEntry log : loaded.getLogger().getEntries()) {
                System.out.println(" - " + log);
            }

            return true;

        } catch (Exception e) {
            System.out.println("❌ خطای تست: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}