public class DefensiveStructure extends Building {
    private int defensePower;

    public DefensiveStructure(Castle owner) {
        super("Defensive Structure", 60, owner);
        this.defensePower = 10; // قدرت پایه دفاع
    }

    public int getDefensePower() {
        return defensePower;
    }

    @Override
    protected void onUpgrade() {
        defensePower += 5;
    }
}