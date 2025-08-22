public interface Upgradable {
    void upgrade() throws NotEnoughGoldException, MaxLevelReachedException;
}