public class Lobby {
    private String[] slots = new String[4];

    public boolean addPlayer(String username) {
        if (isInLobby(username)) return true;
        for (int i = 0; i < 4; i++) {
            if (slots[i] == null) {
                slots[i] = username;
                return true;
            }
        }
        return false;
    }

    public void removePlayer(String username) {
        for (int i = 0; i < 4; i++) {
            if (username != null && username.equals(slots[i])) {
                slots[i] = null;
            }
        }
    }

    public boolean isInLobby(String username) {
        for (String s : slots) {
            if (username != null && username.equals(s)) return true;
        }
        return false;
    }

    public String getSlotName(int index) {
        return slots[index];
    }

    public int getPlayerCount() {
        int count = 0;
        for (String s : slots) {
            if (s != null) count++;
        }
        return count;
    }

    public boolean canStartGame() {
        return getPlayerCount() >= 2;
    }

    public String[] getAll() {
        return slots;
    }

    public void clear() {
        for (int i = 0; i < 4; i++) slots[i] = null;
    }
}