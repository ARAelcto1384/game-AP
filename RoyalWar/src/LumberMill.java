public class LumberMill extends Building {

    public LumberMill(Castle owner) {
        super("Lumber Mill", 35, owner);
    }

    @Override
    protected void onUpgrade() {
        // Increase wood production
    }
}