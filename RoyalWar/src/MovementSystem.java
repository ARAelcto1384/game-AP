public class MovementSystem {
    public static void tryMove(Entity entity, Direction dir, GameMap map)
            throws InvalidMoveException, MovementBlockException {

        Position target = entity.getPosition().moved(dir);
        int tx = target.getX();
        int ty = target.getY();

        if (!map.isInside(tx, ty)) {
            throw new InvalidMoveException("The movement is outside the map range!");
        }

        if (map.isObstacle(tx, ty)) {
            throw new MovementBlockException("Risk of hitting an obstacle!");
        }

        entity.setPosition(target);
    }
}