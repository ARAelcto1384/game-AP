public class Lobby {
    private String[] slots = new String[4];

    public boolean addPlayer(String username) {
        if (isInLobby(username)) return true;
        for (int i = 0; i < 4; i++) {
            if (this.slots[i] == null) {
                this.slots[i] = username;
                return true;
            }
        }
        return false; //lobby is full!
    }

    public void removePlayer(String username) {
        for (int i = 0; i < 4; i++) {
            if (username != null && username.equals(this.slots[i])) {
                this.slots[i] = null;
            }
        }
    }

    public boolean isInLobby(String username) {
        for (String s : this.slots) {
            if (username != null && username.equals(s)) return true;
        }
        return false;
    }

    public String getSlotName(int index) {
        return this.slots[index];
    }

    public int getPlayerCount() {
        int count = 0;
        for (String s : this.slots) {
            if (s != null) count++;
        }
        return count;
    }

    public boolean canStartGame() {
        return getPlayerCount() >= 2;
    }

    public String[] getAll() {
        return this.slots;
    }
}