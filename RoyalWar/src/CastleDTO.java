import java.io.Serializable;
import java.util.Map;

public class CastleDTO implements Serializable {
    private int ownerId;
    private Position position;
    private int health;
    private Map<ResourceType, Integer> resources;

    public CastleDTO(int ownerId, Position position, int health, Map<ResourceType, Integer> resources) {
        this.ownerId = ownerId;
        this.position = position;
        this.health = health;
        this.resources = resources;
    }

    public int getOwnerId() { return ownerId; }
    public Position getPosition() { return position; }
    public int getHealth() { return health; }
    public Map<ResourceType, Integer> getResources() { return resources; }
}