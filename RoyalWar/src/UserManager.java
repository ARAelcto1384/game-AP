import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private List<User> users = new ArrayList<>();
    private final String filePath = "data/users.txt";

    public UserManager() {
        loadUsers();
    }

    private void loadUsers() {
        users.clear();
        File f = new File(filePath);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    users.add(new User(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user file!");
        }
    }

    private void saveUsers() {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (User u : users) {
                bw.write(u.getUsername() + "," + u.getPassword());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving users!");
        }
    }

    public boolean isUsernameTaken(String username) {
        return users.stream().anyMatch(u -> u.getUsername().equals(username));
    }

    public boolean register(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty())  return false;
        if (isUsernameTaken(username)) return false;

        users.add(new User(username, password));
        saveUsers();
        return true;
    }

    public boolean login(String username, String password) {
        return users.stream().anyMatch(u ->
                u.getUsername().equals(username) && u.getPassword().equals(password));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}