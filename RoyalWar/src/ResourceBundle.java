import java.util.EnumMap;

public class ResourceBundle {
    private EnumMap<ResourceType, Integer> resources;

    public ResourceBundle() {
        resources = new EnumMap<>(ResourceType.class);
        for (ResourceType rt : ResourceType.values()) {
            resources.put(rt, 0);
        }
    }

    public int get(ResourceType type) {
        return resources.get(type);
    }

    public void add(ResourceType type, int amount) {
        resources.put(type, resources.get(type) + amount);
    }

    public boolean consume(ResourceType type, int amount) {
        if (resources.get(type) >= amount) {
            resources.put(type, resources.get(type) - amount);
            return true;
        }
        return false;
    }

    public EnumMap<ResourceType, Integer> getAll() {
        return resources;
    }
}