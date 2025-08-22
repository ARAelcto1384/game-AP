public class MonsterStronghold {
    private Position center;

    public MonsterStronghold(int mapSize) {
        this.center = new Position(mapSize / 2, mapSize / 2);
    }

    public Position getCenter() {
        return center;
    }
}