import java.io.Serializable;
import java.time.LocalDateTime;

public class GameLogEntry implements Serializable {
    private LocalDateTime timestamp;
    private String message;

    public GameLogEntry(String message) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + message;
    }
}