import java.io.Serializable;

public class PlayerDTO implements Serializable {
    private int id;
    private String name;
    private Position position;
    private int score;
    private int actionPoints;

    public PlayerDTO(int id, String name, Position position, int score, int actionPoints) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.score = score;
        this.actionPoints = actionPoints;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Position getPosition() { return position; }
    public int getScore() { return score; }
    public int getActionPoints() { return actionPoints; }
}