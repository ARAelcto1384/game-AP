public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    public static Direction fromKey(char c) {
        char k = Character.toUpperCase(c);
        if (k == 'W') return UP;
        if (k == 'S') return DOWN;
        if (k == 'A') return LEFT;
        if (k == 'D') return RIGHT;
        return null;
    }
}