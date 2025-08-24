public interface GameEventListener {
    default void onPlayerMoved(Player player, Position newPos) {}
    default void onTurnEnded(Player prevPlayer, int nextPlayerId, int round) {}
    default void onCastleCaptured(Castle castle, Player newOwner) {}
    default void onGameEnded(Player winner, String reason) {}
    default void onEventTriggered(Event event) {}
    default void onBattleStarted(Attack attack) {}
    default void onBattleResolved(Attack attack) {}
}