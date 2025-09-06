import java.io.Serializable;

public class Position implements Serializable {
    private int x;
    private int y;

    public Position(int x, int y) { this.x = x; this.y = y; }

    public int getX() { return x; }
    public int getY() { return y; }

    public Position moved(Direction dir) {
        if (dir == Direction.UP)    return new Position(x, y - 1);
        if (dir == Direction.DOWN)  return new Position(x, y + 1);
        if (dir == Direction.LEFT)  return new Position(x - 1, y);
        if (dir == Direction.RIGHT) return new Position(x + 1, y);
        return new Position(x, y);
    }

    public int manhattanTo(Position other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }
}