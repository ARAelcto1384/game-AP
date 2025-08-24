public class GameConfig {
    public static final int MAP_SIZE = 100;

    // فاصله‌ها
    public static final int MIN_DIST_FROM_CENTER = 20;
    public static final int MIN_DIST_BETWEEN_CASTLES = 15;

    // تعداد بازیکن
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 4;

    // سمبل‌ها
    public static final char SYMBOL_EMPTY = '.';
    public static final char SYMBOL_OBSTACLE = '#';
    public static final char SYMBOL_MONSTER = 'M';
    public static final char SYMBOL_CASTLE_P1 = '1';
    public static final char SYMBOL_CASTLE_P2 = '2';
    public static final char SYMBOL_CASTLE_P3 = '3';
    public static final char SYMBOL_CASTLE_P4 = '4';

    // نبرد
    public static final int MIN_DISTANCE_TO_ATTACK_CASTLE = 3;
    public static final int MIN_DISTANCE_TO_ATTACK_MONSTER = 2;
    public static final int MONSTER_DEFENSE_BASE = 20;
    public static final int MONSTER_KILL_SCORE = 10;
    public static final int RAID_STEAL_PERCENT = 20;
    public static final int CAPTURE_RESTORE_HEALTH = 50;
}