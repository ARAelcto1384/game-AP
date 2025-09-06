import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {
    private GameMap map;
    private List<Player> players = new ArrayList<>();
    private MonsterStronghold stronghold;

    private TurnManager turnManager = new TurnManager();
    private MonsterAI monsterAI = new MonsterAI();
    private ProductionSystem productionSystem = new ProductionSystem();
    private BattleManager battleManager = new BattleManager();
    private EventManager eventManager = new EventManager();
    private EndgameManager endgameManager = new EndgameManager();

    private List<Castle> castles = new ArrayList<>();
    private List<Market> markets = new ArrayList<>();

    private GameState gameState = GameState.RUNNING;
    private Player winner = null;
    private String winReason = "";

    private Random rnd = new Random();

    private GameLogger logger = new GameLogger();
    public GameLogger getLogger() { return logger; }

    public GameManager(List<String> playerNames) {
        if (playerNames == null
        || playerNames.size() < GameConfig.MIN_PLAYERS
        || playerNames.size() > GameConfig.MAX_PLAYERS) {
            throw new IllegalArgumentException("The number of players should be between 2 and 4!");
        }
        initCore(playerNames);
    }

    private void initCore(List<String> playerNames) {
        this.map = new GameMap();

        MapPersistence io = new MapPersistence();
        io.ensureMapFileExists(GameConfig.MAP_SIZE);
        io.loadObstaclesInto(map);

        map.placeMonsterStronghold();
        this.stronghold = new MonsterStronghold(map.getSize());

        for (int i = 0; i < playerNames.size(); i++) {
            int id = i + 1;
            String name = playerNames.get(i);
            players.add(new Player(id, name, null, null));
        }

        List<Position> positions = pickCastlePositions(players.size());
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            Position pos = positions.get(i);
            map.placePlayerCastle(p.getId(), pos.getX(), pos.getY());
            setPlayerStartAndCastle(p, pos);
        }

        turnManager.configurePlayersCount(players.size());

        turnManager.addListener(monsterAI);
        turnManager.addListener(productionSystem);
        turnManager.addListener(battleManager);
        turnManager.addListener(eventManager);

        turnManager.startFirstRound(this);
    }

    private List<Position> pickCastlePositions(int n) {
        List<Position> out = new ArrayList<>();
        Position center = new Position(GameConfig.MAP_SIZE / 2, GameConfig.MAP_SIZE / 2);

        while (out.size() < n) {
            int x = rnd.nextInt(map.getSize());
            int y = rnd.nextInt(map.getSize());
            if (!map.isInside(x, y)) continue;
            if (!map.canPlaceCastleAt(x, y)) continue;

            Position p = new Position(x, y);
            if (p.manhattanTo(center) < GameConfig.MIN_DIST_FROM_CENTER) continue;

            boolean ok = true;
            for (Position q : out) {
                if (p.manhattanTo(q) < GameConfig.MIN_DIST_BETWEEN_CASTLES) {
                    ok = false; break;
                }
            }
            if (ok) out.add(p);
        }
        return out;
    }

    private void setPlayerStartAndCastle(Player p, Position castlePos) {
        Player rebuilt = new Player(p.getId(), p.getName(), castlePos, castlePos);
        int idx = p.getId() - 1;
        players.set(idx, rebuilt);

        Castle c = new Castle(rebuilt, castlePos);
        castles.add(c);
        markets.add(new Market(c));
    }

    public GameMap getMap() { return map; }
    public MonsterStronghold getStronghold() { return stronghold; }

    public List<Player> getPlayers() { return players; }
    public List<Castle> getCastles() { return castles; }
    public List<Market> getMarkets() { return markets; }

    public TurnManager getTurnManager() { return turnManager; }
    public MonsterAI getMonsterAI() { return monsterAI; }
    public ProductionSystem getProductionSystem() { return productionSystem; }
    public BattleManager getBattleManager() { return battleManager; }
    public EventManager getEventManager() { return eventManager; }
    public EndgameManager getEndgameManager() { return endgameManager; }

    public GameState getGameState() { return gameState; }
    public Player getWinner() { return winner; }
    public String getWinReason() { return winReason; }

    public Player getCurrentPlayer() {
        return players.get(turnManager.getCurrentIndex());
    }

    public Player findPlayerById(int id) {
        for (Player p : players) if (p.getId() == id) return p;
        return null;
    }

    public Castle getCastleOf(Player p) {
        if (p == null) return null;
        for (Castle c : castles) if (c.getOwner().getId() == p.getId()) return c;
        return null;
    }

    public Market getMarketOf(Player p) {
        Castle c = getCastleOf(p);
        if (c == null) return null;
        for (Market m : markets) if (m.getCastle() == c) return m;
        return null;
    }

    public Barracks getBarracksOf(Player p) {
        Castle c = getCastleOf(p);
        return c != null ? c.getBarracks() : null;
    }

    private void ensureRunning() throws GameAlreadyEndedException {
        if (gameState == GameState.ENDED) {
            throw new GameAlreadyEndedException("The game is over!");
        }
    }

    public void moveCurrentPlayer(Direction dir)
            throws  NoActionPointsException,
            InvalidMoveException, MovementBlockException, GameAlreadyEndedException {
        ensureRunning();
        Player p = getCurrentPlayer();
        if (!p.hasActionPoint()) {
            throw new NoActionPointsException("There are no moves left!");
        }

        MovementSystem.tryMove(p, dir, map);
        p.consumeActionPoint();
        logger.log("Player " + p.getName() + " to " + dir + " Moved. (new position:" + p.getPosition() + ")");
        MovementSystem.tryMove(p, dir, map);
        for (GameEventListener l : uiListeners) {
            l.onPlayerMoved(p, p.getPosition());
        }
        p.consumeActionPoint();
    }

    public void endTurn() throws GameAlreadyEndedException {
        ensureRunning();
        Player ended = getCurrentPlayer();
        turnManager.endTurn(this);
        for (GameEventListener l : uiListeners) {
            l.onTurnEnded(ended, getCurrentPlayer().getId(), turnManager.getCurrentRound());
        }
        logger.log(getCurrentPlayer().getName() + "'s turn is over!");
        turnManager.endTurn(this);
    }

    public void onMonsterAttackScheduled(int round, int effectivePower) {
        if (players.size() <= 1) return;
        Player current = getCurrentPlayer();

        Player targetP = null;
        int tries = 0;
        while (tries < 10) {
            Player candidate = players.get(rnd.nextInt(players.size()));
            if (candidate.getId() != current.getId()) {
                targetP = candidate; break;
            }
            tries++;
        }
        if (targetP == null) return;

        Castle target = getCastleOf(targetP);
        Attack a = Attack.monsterAttack(target, effectivePower, round);
        battleManager.schedule(a);
    }

    public void initiateAttackOnCastle(int targetPlayerId, AttackType type,
                                       int soldiers, int archers, int cavalry, int spies, int merchants)
            throws TooFarToAttackException, InvalidTargetException,
            UnitNotAvailableException, GameAlreadyEndedException {

        ensureRunning();

        Player attacker = getCurrentPlayer();
        Player defender = findPlayerById(targetPlayerId);

        if (defender == null || defender.getId() == attacker.getId()) {
            throw new InvalidTargetException("The target is invalid!");
        }

        Castle attackerCastle = getCastleOf(attacker);
        Castle defenderCastle = getCastleOf(defender);

        if (type != AttackType.RAID && type != AttackType.CONQUER) {
            throw new InvalidTargetException("The castle attack type is invalid!");
        }

        int dist = attacker.getPosition().manhattanTo(defenderCastle.getPosition());
        if (dist > GameConfig.MIN_DISTANCE_TO_ATTACK_CASTLE) {
            throw new TooFarToAttackException("To attack the enemy castle, you need to get closer!");
        }

        Barracks b = attackerCastle.getBarracks();
        List<Unit> payload = new ArrayList<>();
        payload.addAll(b.takeUnits(UnitType.SOLDIER, soldiers));
        payload.addAll(b.takeUnits(UnitType.ARCHER, archers));
        payload.addAll(b.takeUnits(UnitType.CAVALRY, cavalry));
        payload.addAll(b.takeUnits(UnitType.SPY, spies));
        payload.addAll(b.takeUnits(UnitType.MERCHANT, merchants));

        int resolveAt = getTurnManager().getCurrentRound() + 1;
        Attack a = new Attack(type, attacker, defenderCastle, resolveAt);
        a.addUnits(payload);
        battleManager.schedule(a);
    }

    // Attack on the monster castle
    public void initiateAttackOnMonster(int soldiers, int archers, int cavalry, int spies, int merchants)
            throws TooFarToAttackException, UnitNotAvailableException, GameAlreadyEndedException {

        ensureRunning();

        Player attacker = getCurrentPlayer();
        Castle attackerCastle = getCastleOf(attacker);

        int dist = attacker.getPosition().manhattanTo(stronghold.getCenter());
        if (dist > GameConfig.MIN_DISTANCE_TO_ATTACK_MONSTER) {
            throw new TooFarToAttackException("To attack the monster, you must be near the central castle!");
        }

        Barracks b = attackerCastle.getBarracks();
        List<Unit> payload = new ArrayList<>();
        payload.addAll(b.takeUnits(UnitType.SOLDIER, soldiers));
        payload.addAll(b.takeUnits(UnitType.ARCHER, archers));
        payload.addAll(b.takeUnits(UnitType.CAVALRY, cavalry));
        payload.addAll(b.takeUnits(UnitType.SPY, spies));
        payload.addAll(b.takeUnits(UnitType.MERCHANT, merchants));

        int resolveAt = getTurnManager().getCurrentRound() + 1;
        Attack a = new Attack(AttackType.MONSTER, attacker, null, resolveAt);
        a.addUnits(payload);
        battleManager.schedule(a);
    }

    // Change castle ownership
    public void changeCastleOwner(Castle castle, Player newOwner) {
        castle.setOwner(newOwner);
        logger.log("The castle at " + castle.getPosition() + "s location became the property of " + newOwner.getName() + "!");
        map.placePlayerCastle(newOwner.getId(), castle.getPosition().getX(), castle.getPosition().getY());
        checkWinCondition();
        castle.setOwner(newOwner);
        map.placePlayerCastle(newOwner.getId(), castle.getPosition().getX(), castle.getPosition().getY());
        for (GameEventListener l : uiListeners) {
            l.onCastleCaptured(castle, newOwner);
        }
        checkWinCondition();
    }

    public void checkWinCondition() {
        endgameManager.checkAndEndIfNeeded(this);
    }

    private List<GameEventListener> uiListeners = new ArrayList<>();
    public void addUIListener(GameEventListener l) { if (l != null) uiListeners.add(l); }
    public void removeUIListener(GameEventListener l) { uiListeners.remove(l); }
    public List<GameEventListener> getUIListeners() { return uiListeners; }

    public void endGame(Player winner, String reason) {
        logger.log("Game over! Winner:" + winner.getName() + " | Reason: " + reason);
        if (gameState == GameState.ENDED) return;
        this.gameState = GameState.ENDED;
        this.winner = winner;
        this.winReason = reason;
        for (GameEventListener l : uiListeners) {
            l.onGameEnded(winner, reason);
        }
    }
}