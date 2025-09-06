public class ProductionSystem implements RoundListener {
    @Override
    public void onRoundStart(int round, GameManager gm) {
        gm.getLogger().log("Round start: " + round);
        for (Castle c : gm.getCastles()) {
            c.produceResources();
            c.addFlag();
        }
        gm.checkWinCondition();
    }
}