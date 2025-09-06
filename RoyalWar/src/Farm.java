public class Farm extends Building {

    public Farm(Castle owner) {
        super("Farm", 30, owner);
    }

    @Override
    protected void onUpgrade() {
        // Increase food production
    }
}