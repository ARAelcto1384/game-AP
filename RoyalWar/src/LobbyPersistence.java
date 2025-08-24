import java.io.*;

public class LobbyPersistence {
    private final String filePath = "data/lobby.txt";

    public void save(Lobby lobby) {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            String[] slots = lobby.getAll();
            for (int i = 0; i < 4; i++) {
                bw.write(slots[i] == null ? "-" : slots[i]);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("❌ خطا در ذخیره لابی.");
        }
    }

    public void load(Lobby lobby) {
        File f = new File(filePath);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            for (int i = 0; i < 4; i++) {
                String line = br.readLine();
                if (line == null) break;
                lobby.getAll()[i] = "-".equals(line) ? null : line;
            }
        } catch (IOException e) {
            System.out.println("❌ خطا در خواندن لابی.");
        }
    }
}