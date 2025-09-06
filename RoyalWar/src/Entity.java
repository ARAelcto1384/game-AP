public abstract class Entity implements Movable {
    protected Position position;

    public Entity(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public void move(Direction dir, GameMap map) throws InvalidMoveException {
        if (dir == null) throw new InvalidMoveException("The direction is invalid!");
        Position next = position.moved(dir);
        int x = next.getX();
        int y = next.getY();

        if (!map.isInside(x, y)) throw new InvalidMoveException("The move is out of the map!");
        if (map.getCell(x, y).getType() == CellType.OBSTACLE)
            throw new InvalidMoveException("Risk of hitting an obstacle!");

        this.position = next;
    }
}