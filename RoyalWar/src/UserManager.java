import java.io.*;
import java.util.ArrayList;

public class UserManager {
    private ArrayList<User> users = new ArrayList<>();
    private String filePath = "data/users.txt";

    public UserManager() {
        loadUsers();
    }

    private void loadUsers() {
        this.users.clear();
        try {
            File f = new File(this.filePath);
            if (!f.exists()) return;
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    this.users.add(new User(parts[0], parts[1]));
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error in reading users file!");
        }
    }

    private void saveUsers() {
        try {
            File dir = new File("data");
            if (!dir.exists()) dir.mkdirs();
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.filePath));
            for (User u : this.users) {
                bw.write(u.getUsername() + "," + u.getPassword());
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Error in saving users file!");
        }
    }

    public boolean isUsernameTaken(String username) {
        for (User u : this.users) {
            if (u.getUsername().equals(username)) return true;
        }
        return false;
    }

    public boolean register(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) return false;
        if (isUsernameTaken(username)) return false;
        this.users.add(new User(username, password));
        saveUsers();
        return true;
    }

    public boolean login(String username, String password) {
        for (User u : this.users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }
}