import java.io.*;

public class LobbyPersistence {
    private String filePath = "data/lobby.txt";

    public void save(Lobby lobby) {
        try {
            File dir = new File("data");
            if (!dir.exists()) dir.mkdirs();
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.filePath));
            String[] slots = lobby.getAll();
            for (int i = 0; i < 4; i++) {
                bw.write(slots[i] == null ? "-" : slots[i]);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Error in saving lobby!");
        }
    }

    public void load(Lobby lobby) {
        try {
            File f = new File(this.filePath);
            if (!f.exists()) return;
            BufferedReader br = new BufferedReader(new FileReader(f));
            for (int i = 0; i < 4; i++) {
                String line = br.readLine();
                if (line == null) break;
                lobby.getAll()[i] = "-".equals(line) ? null : line;
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error in loading lobby!");
        }
    }
}