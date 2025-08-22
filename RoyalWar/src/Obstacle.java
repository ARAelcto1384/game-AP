public class Obstacle {
    private Position position;
    private String description;

    public Obstacle(Position position, String description) {
        this.position = position;
        this.description = description;
    }

    public Position getPosition() { return position; }
    public String getDescription() { return description; }
}