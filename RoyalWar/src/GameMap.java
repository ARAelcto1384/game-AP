import java.util.Random;

public class GameMap {
    private int size = GameConfig.MAP_SIZE;
    private Cell[][] grid = new Cell[size][size];

    public GameMap() {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                grid[y][x] = new Cell();
            }
        }
    }

    public int getSize() { return size; }

    public boolean isInside(int x, int y) {
        return x >= 0 && y >= 0 && x < size && y < size;
    }

    public Cell getCell(int x, int y) {
        return grid[y][x];
    }

    public boolean isObstacle(int x, int y) {
        return grid[y][x].getType() == CellType.OBSTACLE;
    }

    public void setObstacle(int x, int y) {
        if (isInside(x, y)) grid[y][x].setType(CellType.OBSTACLE);
    }

    public void addObstacle(Position pos) {
        if (isInside(pos.getX(), pos.getY())) {
            grid[pos.getY()][pos.getX()].setType(CellType.OBSTACLE);
        }
    }

    public void placeMonsterStronghold() {
        // مرکز 2×2 برای نقشه زوج
        int c1 = size / 2 - 1;
        int c2 = size / 2;
        setStrongholdCell(c1, c1);
        setStrongholdCell(c1, c2);
        setStrongholdCell(c2, c1);
        setStrongholdCell(c2, c2);
    }

    private void setStrongholdCell(int x, int y) {
        if (isInside(x, y)) {
            Cell cell = grid[y][x];
            cell.setType(CellType.MONSTER_STRONGHOLD);
            cell.setOwnerId(0);
        }
    }

    public boolean canPlaceCastleAt(int x, int y) {
        if (!isInside(x, y)) return false;
        CellType t = grid[y][x].getType();
        return t == CellType.EMPTY;
    }

    public void placePlayerCastle(int playerId, int x, int y) {
        Cell cell = grid[y][x];
        cell.setType(CellType.PLAYER_CASTLE);
        cell.setOwnerId(playerId);
    }

    public Position randomCastlePositionFarFromCenter(Random rnd) {
        Position center = new Position(size / 2, size / 2);
        while (true) {
            int x = rnd.nextInt(size);
            int y = rnd.nextInt(size);
            if (!isInside(x, y)) continue;
            if (!canPlaceCastleAt(x, y)) continue;
            Position p = new Position(x, y);
            if (p.manhattanTo(center) >= GameConfig.MIN_DIST_FROM_CENTER) {
                return p;
            }
        }
    }
}