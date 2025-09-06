import java.util.Random;

public class MonsterAI implements RoundListener {

    private int attackIntervalRounds = 3;
    private int energy = 100;
    private int baseAttackPower = 10;
    private Random rnd = new Random();

    @Override
    public void onRoundStart(int round, GameManager gm) {
        int effectivePower = (int) Math.max(1, baseAttackPower * (energy / 100.0));

        if (attackIntervalRounds > 0 && round % attackIntervalRounds == 0) {
            // Choose a castle randomly from all players except the current one
            Player current = gm.getCurrentPlayer();
            Player target = null;
            int tries = 0;
            while (tries < 10) {
                Player candidate = gm.getPlayers().get(rnd.nextInt(gm.getPlayers().size()));
                if (candidate.getId() != current.getId()) {
                    target = candidate; break;
                }
                tries++;
            }
            if (target != null) {
                Castle targetCastle = gm.getCastleOf(target);
                gm.getBattleManager().schedule(
                        Attack.monsterAttack(targetCastle, effectivePower, round)
                );
            }
        }

        if (energy > 0) energy -= 1;
    }
}