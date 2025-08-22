public class ProductionSystem implements RoundListener {

    @Override
    public void onRoundStart(int round, GameManager gm) {
        Castle c1 = gm.getCastle1();
        Castle c2 = gm.getCastle2();

        // تولید منابع و افزودن پرچم برای قلعه 1
        if (c1 != null) {
            c1.produceResources();  // داخل Castle ضریب رضایت و بوست‌ها اعمال می‌شود
            c1.addFlag();
        }

        // تولید منابع و افزودن پرچم برای قلعه 2
        if (c2 != null) {
            c2.produceResources();
            c2.addFlag();
        }

        // بررسی پایان بازی بعد از اعمال تغییرات منابع و پرچم‌ها
        gm.checkWinCondition();
    }
}