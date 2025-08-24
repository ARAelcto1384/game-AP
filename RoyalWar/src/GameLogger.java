import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameLogger implements Serializable {

    private List<GameLogEntry> entries = new ArrayList<>();

    public void log(String msg) {
        GameLogEntry entry = new GameLogEntry(msg);
        entries.add(entry);
        System.out.println(entry); // چاپ همزمان در کنسول
    }

    public List<GameLogEntry> getEntries() { return entries; }

    public void clear() { entries.clear(); }
}