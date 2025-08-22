public class Cell {
    private CellType type = CellType.EMPTY;
    private int ownerId = 0; // 0: ندارد، 1: بازیکن1، 2: بازیکن2

    public CellType getType() { return type; }
    public void setType(CellType type) { this.type = type; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public char toSymbol() {
        if (type == CellType.OBSTACLE) return GameConfig.SYMBOL_OBSTACLE;
        if (type == CellType.MONSTER_STRONGHOLD) return GameConfig.SYMBOL_MONSTER;
        if (type == CellType.PLAYER_CASTLE) {
            return ownerId == 1 ? GameConfig.SYMBOL_CASTLE_P1 : GameConfig.SYMBOL_CASTLE_P2;
        }
        return GameConfig.SYMBOL_EMPTY;
    }
}