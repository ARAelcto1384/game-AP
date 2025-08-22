public class GameConfig {
    public static final int MAP_SIZE = 100;

    public static final int MIN_DIST_FROM_CENTER = 20;

    public static final char SYMBOL_EMPTY = '.';
    public static final char SYMBOL_OBSTACLE = '#';
    public static final char SYMBOL_MONSTER = 'M';
    public static final char SYMBOL_CASTLE_P1 = '1';
    public static final char SYMBOL_CASTLE_P2 = '2';

    // نبرد
    public static final int MIN_DISTANCE_TO_ATTACK_CASTLE = 3; // فاصله منهتن مجاز برای شروع حمله به قلعه
    public static final int MIN_DISTANCE_TO_ATTACK_MONSTER = 2; // برای حمله به هیولا (دژ مرکزی)
    public static final int MONSTER_DEFENSE_BASE = 20; // دفاع پایه هیولا در نبردهای بازیکن علیه هیولا
    public static final int MONSTER_KILL_SCORE = 10;   // امتیاز برای شکست هیولا

    public static final int RAID_STEAL_PERCENT = 20;   // درصد غنیمت از منابع قلعه مدافع در حمله Raid
    public static final int CAPTURE_RESTORE_HEALTH = 50; // درصد سلامت پس از تصرف قلعه
}