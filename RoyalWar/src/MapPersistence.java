import java.io.*;

public class MapPersistence {
    private String filePath = "data/map.txt";

    public void ensureMapFileExists(int size) {
        try {
            File dir = new File("data");
            if (!dir.exists()) dir.mkdirs();

            File f = new File(filePath);
            if (!f.exists()) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        bw.write(GameConfig.SYMBOL_EMPTY);
                    }
                    bw.newLine();
                }
                bw.close();
            }
        } catch (IOException e) {
            System.out.println("Error creating map file!");
        }
    }

    public void loadObstaclesInto(GameMap map) {
        try {
            File f = new File(filePath);
            if (!f.exists()) return;

            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            int y = 0;
            while ((line = br.readLine()) != null && y < map.getSize()) {
                for (int x = 0; x < line.length() && x < map.getSize(); x++) {
                    if (line.charAt(x) == GameConfig.SYMBOL_OBSTACLE) {
                        map.setObstacle(x, y);
                    }
                }
                y++;
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error reading map file!");
        }
    }
}