public interface Movable {
    void move(Direction dir, GameMap map) throws InvalidMoveException;
}