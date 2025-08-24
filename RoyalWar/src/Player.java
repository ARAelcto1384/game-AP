public class Player extends Entity {
    private int id;
    private String name;
    private Position castlePosition;

    private int actionPoints = 0;
    private static final int ACTIONS_PER_TURN = 1;

    private int score = 0; // امتیاز بازیکن

    public Player(int id, String name, Position startPos, Position castlePos) {
        super(startPos);
        this.id = id;
        this.name = name;
        this.castlePosition = castlePos;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Position getCastlePosition() { return castlePosition; }

    public boolean hasActionPoint() { return actionPoints > 0; }
    public void resetActionForTurn() { this.actionPoints = ACTIONS_PER_TURN; }
    public void consumeActionPoint() throws NoActionPointsException {
        if (actionPoints <= 0) throw new NoActionPointsException("هیچ حرکت باقی نمانده است.");
        actionPoints--;
    }

    public int getScore() { return score; }
    public void addScore(int s) { score += s; }

    public void setPosition(Position pos) { this.position = pos; }
    public void setScore(int s) { this.score = s; }
}

