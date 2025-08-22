public class Mine extends Building {

    public Mine(Castle owner) {
        super("Stone Mine", 40, owner);
    }

    @Override
    protected void onUpgrade() {
        // افزایش تولید سنگ
    }
}