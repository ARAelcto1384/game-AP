public class MovementSystem {

    // تلاش برای حرکت یک موجودیت روی نقشه
    public static void tryMove(Entity entity, Direction dir, GameMap map)
            throws InvalidMoveException, MovementBlockException {

        Position target = entity.getPosition().moved(dir);
        int tx = target.getX();
        int ty = target.getY();

        if (!map.isInside(tx, ty)) {
            throw new InvalidMoveException("حرکت خارج از محدوده نقشه است.");
        }

        if (map.isObstacle(tx, ty)) {
            throw new MovementBlockException("خانه مقصد یک مانع است و قابل عبور نیست.");
        }

        // آینده: بررسی برخورد با قلعه‌ها، هیولا یا بازیکن دیگر
        entity.setPosition(target);
    }
}