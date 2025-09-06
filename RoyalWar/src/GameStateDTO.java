import java.io.Serializable;
import java.util.List;

public class GameStateDTO implements Serializable {
    private int currentRound;
    private int currentPlayerIndex;
    private List<PlayerDTO> players;
    private List<CastleDTO> castles;

    public GameStateDTO(int currentRound, int currentPlayerIndex,
                        List<PlayerDTO> players, List<CastleDTO> castles) {
        this.currentRound = currentRound;
        this.currentPlayerIndex = currentPlayerIndex;
        this.players = players;
        this.castles = castles;
    }

    public int getCurrentRound() { return currentRound; }
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
    public List<PlayerDTO> getPlayers() { return players; }
    public List<CastleDTO> getCastles() { return castles; }
}