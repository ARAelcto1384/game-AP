import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SaveLoadManager {

    private static final String SAVE_FILE = "royalwar.sav";

    public static void save(GameManager gm) throws IOException {
        List<PlayerDTO> playerDTOs = gm.getPlayers().stream()
                .map(p -> new PlayerDTO(p.getId(), p.getName(), p.getPosition(), p.getScore(), p.hasActionPoint() ? 1 : 0))
                .collect(Collectors.toList());

        List<CastleDTO> castleDTOs = gm.getCastles().stream()
                .map(c -> new CastleDTO(c.getOwner().getId(), c.getPosition(), c.getHealth(), c.getResources().toMap()))
                .collect(Collectors.toList());

        GameStateDTO state = new GameStateDTO(
                gm.getTurnManager().getCurrentRound(),
                gm.getTurnManager().getCurrentIndex(),
                playerDTOs,
                castleDTOs
        );

        List<GameLogEntry> logs = gm.getLogger().getEntries();

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            out.writeObject(state);
        }
    }

    public static GameManager load() throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            GameStateDTO state = (GameStateDTO) in.readObject();
            return rebuildGameFromState(state);
        }
    }

    private static GameManager rebuildGameFromState(GameStateDTO state) {
        List<String> names = state.getPlayers().stream().map(PlayerDTO::getName).collect(Collectors.toList());
        GameManager gm = new GameManager(names);

        // بازگرداندن وضعیت
        for (int i = 0; i < gm.getPlayers().size(); i++) {
            Player p = gm.getPlayers().get(i);
            PlayerDTO pd = state.getPlayers().get(i);
            p.setPosition(pd.getPosition());
            p.setScore(pd.getScore());
            if (pd.getActionPoints() > 0) p.resetActionForTurn();
        }

        for (int i = 0; i < gm.getCastles().size(); i++) {
            Castle c = gm.getCastles().get(i);
            CastleDTO cd = state.getCastles().get(i);
            c.setOwner(gm.findPlayerById(cd.getOwnerId()));
            c.setPosition(cd.getPosition());
            c.setHealth(cd.getHealth());
            c.getResources().loadFromMap(cd.getResources());
        }

        gm.getTurnManager().setCurrentRound(state.getCurrentRound());
        gm.getTurnManager().setCurrentIndex(state.getCurrentPlayerIndex());
        return gm;
    }
}