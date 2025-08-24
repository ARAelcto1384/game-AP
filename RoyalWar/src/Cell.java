public class Cell {
    private CellType type = CellType.EMPTY;
    private int ownerId = 0; // 0: ندارد، 1..4: بازیکنان

    public CellType getType() { return type; }
    public void setType(CellType type) { this.type = type; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public char toSymbol() {
        if (type == CellType.OBSTACLE) return GameConfig.SYMBOL_OBSTACLE;
        if (type == CellType.MONSTER_STRONGHOLD) return GameConfig.SYMBOL_MONSTER;
        if (type == CellType.PLAYER_CASTLE) {
            if (ownerId == 1) return GameConfig.SYMBOL_CASTLE_P1;
            if (ownerId == 2) return GameConfig.SYMBOL_CASTLE_P2;
            if (ownerId == 3) return GameConfig.SYMBOL_CASTLE_P3;
            if (ownerId == 4) return GameConfig.SYMBOL_CASTLE_P4;
        }
        return GameConfig.SYMBOL_EMPTY;
    }
}