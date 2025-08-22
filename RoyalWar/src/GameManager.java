import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {
    private GameMap map;
    private Player player1;
    private Player player2;
    private MonsterStronghold stronghold;

    private TurnManager turnManager = new TurnManager();
    private MonsterAI monsterAI = new MonsterAI();
    private ProductionSystem productionSystem = new ProductionSystem();
    private BattleManager battleManager = new BattleManager();
    private EventManager eventManager = new EventManager();
    private EndgameManager endgameManager = new EndgameManager();

    private Castle castle1;
    private Castle castle2;
    private Market market1;
    private Market market2;

    private GameState gameState = GameState.RUNNING;
    private Player winner = null;
    private String winReason = "";

    private Random rnd = new Random();

    public GameManager() {
        initCore("Player1", "Player2");
    }

    public GameManager(String player1Name, String player2Name) {
        initCore(player1Name, player2Name);
    }

    private void initCore(String p1Name, String p2Name) {
        this.map = new GameMap();

        MapPersistence io = new MapPersistence();
        io.ensureMapFileExists(GameConfig.MAP_SIZE);
        io.loadObstaclesInto(map);

        map.placeMonsterStronghold();
        this.stronghold = new MonsterStronghold(map.getSize());

        Position c1Pos = map.randomCastlePositionFarFromCenter(rnd);
        Position c2Pos = map.randomCastlePositionFarFromCenter(rnd);
        while (c2Pos.getX() == c1Pos.getX() && c2Pos.getY() == c1Pos.getY()) {
            c2Pos = map.randomCastlePositionFarFromCenter(rnd);
        }

        map.placePlayerCastle(1, c1Pos.getX(), c1Pos.getY());
        map.placePlayerCastle(2, c2Pos.getX(), c2Pos.getY());

        this.player1 = new Player(1, p1Name, c1Pos, c1Pos);
        this.player2 = new Player(2, p2Name, c2Pos, c2Pos);

        this.castle1 = new Castle(player1, c1Pos);
        this.castle2 = new Castle(player2, c2Pos);
        this.market1 = new Market(castle1);
        this.market2 = new Market(castle2);

        // لیسنرهای آغاز راند
        turnManager.addListener(monsterAI);
        turnManager.addListener(productionSystem);
        turnManager.addListener(battleManager);
        turnManager.addListener(eventManager);

        turnManager.startFirstRound(this);
    }

    // Getters
    public GameMap getMap() { return map; }
    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public MonsterStronghold getStronghold() { return stronghold; }
    public TurnManager getTurnManager() { return turnManager; }
    public MonsterAI getMonsterAI() { return monsterAI; }
    public ProductionSystem getProductionSystem() { return productionSystem; }
    public BattleManager getBattleManager() { return battleManager; }
    public EventManager getEventManager() { return eventManager; }
    public EndgameManager getEndgameManager() { return endgameManager; }

    public Castle getCastle1() { return castle1; }
    public Castle getCastle2() { return castle2; }
    public Market getMarket1() { return market1; }
    public Market getMarket2() { return market2; }

    public GameState getGameState() { return gameState; }
    public Player getWinner() { return winner; }
    public String getWinReason() { return winReason; }

    public Player getCurrentPlayer() {
        return (turnManager.getCurrentPlayerId() == 1) ? player1 : player2;
    }

    public Castle getCastleOf(Player p) {
        if (p == null) return null;
        if (castle1.getOwner().getId() == p.getId()) return castle1;
        if (castle2.getOwner().getId() == p.getId()) return castle2;
        return null;
    }

    public Castle getEnemyCastleOf(Player p) {
        if (p == null) return null;
        if (castle1.getOwner().getId() == p.getId()) return castle2;
        return castle1;
    }

    public Barracks getBarracksOf(Player p) {
        Castle c = getCastleOf(p);
        return c != null ? c.getBarracks() : null;
    }

    // کنترل صحت وضعیت قبل از اعمال ورودی‌ها
    private void ensureRunning() throws GameAlreadyEndedException {
        if (gameState == GameState.ENDED) {
            throw new GameAlreadyEndedException("بازی به پایان رسیده است.");
        }
    }

    // حرکت بازیکن
    public void moveCurrentPlayer(Direction dir)
            throws NotYourTurnException, NoActionPointsException,
            InvalidMoveException, MovementBlockException, GameAlreadyEndedException {

        ensureRunning();

        Player p = getCurrentPlayer();

        if (!turnManager.isPlayersTurn(p)) {
            throw new NotYourTurnException("نوبت این بازیکن نیست.");
        }
        if (!p.hasActionPoint()) {
            throw new NoActionPointsException("هیچ حرکت باقی نمانده است.");
        }

        MovementSystem.tryMove(p, dir, map);
        p.consumeActionPoint();
    }

    public void endTurn() throws GameAlreadyEndedException {
        ensureRunning();
        turnManager.endTurn(this);
    }

    public void onMonsterAttackScheduled(int round, int effectivePower) {
        Player other = (turnManager.getCurrentPlayerId() == 1) ? player2 : player1;
        Castle target = getCastleOf(other);
        Attack a = Attack.monsterAttack(target, effectivePower, round);
        battleManager.schedule(a);
    }

    public void initiateAttackOnEnemyCastle(AttackType type,
                                            int soldiers, int archers, int cavalry, int spies, int merchants)
            throws NotYourTurnException, TooFarToAttackException,
            InvalidTargetException, UnitNotAvailableException, GameAlreadyEndedException {

        ensureRunning();

        Player attacker = getCurrentPlayer();
        Castle attackerCastle = getCastleOf(attacker);
        Castle defenderCastle = getEnemyCastleOf(attacker);

        if (type != AttackType.RAID && type != AttackType.CONQUER) {
            throw new InvalidTargetException("نوع حمله به قلعه نامعتبر است.");
        }

        int dist = attacker.getPosition().manhattanTo(defenderCastle.getPosition());
        if (dist > GameConfig.MIN_DISTANCE_TO_ATTACK_CASTLE) {
            throw new TooFarToAttackException("برای حمله به قلعه دشمن باید نزدیک‌تر شوی.");
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

    public void initiateAttackOnMonster(int soldiers, int archers, int cavalry, int spies, int merchants)
            throws TooFarToAttackException, UnitNotAvailableException, GameAlreadyEndedException {

        ensureRunning();

        Player attacker = getCurrentPlayer();
        Castle attackerCastle = getCastleOf(attacker);

        int dist = attacker.getPosition().manhattanTo(stronghold.getCenter());
        if (dist > GameConfig.MIN_DISTANCE_TO_ATTACK_MONSTER) {
            throw new TooFarToAttackException("برای حمله به هیولا باید نزدیک دژ مرکزی باشی.");
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

    // تغییر مالکیت قلعه و بررسی شرط برد
    public void changeCastleOwner(Castle castle, Player newOwner) {
        castle.setOwner(newOwner);
        int ownerId = newOwner.getId();
        map.placePlayerCastle(ownerId, castle.getPosition().getX(), castle.getPosition().getY());
        checkWinCondition();
    }

    // متد عمومی بررسی برد (برای ProductionSystem و دیگر نقاط)
    public void checkWinCondition() {
        endgameManager.checkAndEndIfNeeded(this);
    }

    // ثبت پایان بازی
    public void endGame(Player winner, String reason) {
        if (gameState == GameState.ENDED) return;
        this.gameState = GameState.ENDED;
        this.winner = winner;
        this.winReason = reason;
        // در فاز FXGL: نمایش پیام برد و قفل‌کردن ورودی‌ها
    }
}