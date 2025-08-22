import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EventManager implements RoundListener {

    private List<Integer> eventRounds; // راندهایی که ممکن است رویداد رخ دهد
    private Random rnd = new Random();

    public EventManager() {
        eventRounds = new ArrayList<>();
        // برای نمونه: رویداد ممکن است در راندهای 3، 6، 9 رخ دهد
        eventRounds.add(3);
        eventRounds.add(6);
        eventRounds.add(9);
    }

    @Override
    public void onRoundStart(int round, GameManager gm) {
        if (eventRounds.contains(round)) {
            Event e = randomEvent();
            e.applyEffect(gm.getCastle1(), gm.getCastle2());
            // در گرافیک: اینجا پیام یا انیمیشن نمایش داده می‌شود
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