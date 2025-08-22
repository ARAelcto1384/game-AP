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
        if (dir == null) throw new InvalidMoveException("جهت نامعتبر است.");
        Position next = position.moved(dir);
        int x = next.getX();
        int y = next.getY();

        if (!map.isInside(x, y)) throw new InvalidMoveException("حرکت خارج از نقشه است.");
        if (map.getCell(x, y).getType() == CellType.OBSTACLE)
            throw new InvalidMoveException("خانه مقصد مانع است.");

        this.position = next;
    }
}