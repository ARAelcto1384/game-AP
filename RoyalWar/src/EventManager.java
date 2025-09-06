import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EventManager implements RoundListener {
    private List<Integer> eventRounds;
    private Random rnd = new Random();

    public EventManager() {
        eventRounds = new ArrayList<>();
        eventRounds.add(3);
        eventRounds.add(6);
        eventRounds.add(9);
    }

    @Override
    public void onRoundStart(int round, GameManager gm) {
        if (eventRounds.contains(round)) {
            Event e = randomEvent();
            for (GameEventListener l : gm.getUIListeners()) {
                l.onEventTriggered(e);
            }
            gm.getLogger().log("رویداد: " + e.getName() + " → " + e.getDescription());
            for (Castle c : gm.getCastles()) {
                e.applyTo(c);
            }
        }
    }

    private Event randomEvent() {
        int choice = rnd.nextInt(3);
        switch (choice) {
            case 0: return new FireEvent();
            case 1: return new RainEvent();
            default: return new StormEvent();
        }
    }
}