import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.GameView;
import com.almasb.fxgl.dsl.FXGL;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import java.lang.reflect.Method;
import java.util.*;

import static com.almasb.fxgl.dsl.FXGL.*;

public class RoyalWarApp extends GameApplication {

    private enum State { WELCOME, REGISTER, LOGIN, LOBBY, GAME }
    private State currentState = State.WELCOME;

    // کاربر جاری (برای خروج از لابی)
    private String currentUsername = null;

    // مقیاس و رنگ‌ها
    private static final int TILE = 32; // افزایش سایز برای نمایش بهتر
    private static final String BG_DARK   = "#1f2937";
    private static final String BTN_BLUE  = "#3b82f6";
    private static final String BTN_GREEN = "#10b981";
    private static final String BTN_AMBER = "#f59e0b";
    private static final String TXT_MUTED = "#9ca3af";

    // مدیریت کاربر و لابی
    private final UserManager userManager   = new UserManager();
    private final Lobby lobby               = new Lobby();
    private final LobbyPersistence lobbyIO  = new LobbyPersistence();

    // اسکلت بازی
    private GameManager gm;

    // لایه‌های گرافیکی
    private GameView mapView;       // لایه زمین
    private GameView markersView;   // قلعه‌های آزاد و دژ هیولا
    private GameView playersView;   // مهره بازیکنان

    private Group markersGroup = new Group();
    private Group playersGroup = new Group();
    private final Map<Integer, Node> playerTokens = new HashMap<>();

    // HUD و پنل‌ها
    private Text hudTurnText, hudRoundText, hudInfoText;
    private VBox resourcesPanel;
    private VBox controlsPanel;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1024); // افزایش عرض برای نمایش بهتر پنل‌ها
        settings.setHeight(768); // افزایش ارتفاع
        settings.setTitle("RoyalWar");
        settings.setIntroEnabled(false);
        settings.setMainMenuEnabled(false);
        settings.setDeveloperMenuEnabled(true);
    }

    @Override
    protected void initGame() {
        showWelcomeScreen();
    }

    @Override
    protected void initInput() {
        onKeyDown(javafx.scene.input.KeyCode.W, () -> { if (currentState == State.GAME) tryMove(GameManager.Direction.UP);    });
        onKeyDown(javafx.scene.input.KeyCode.S, () -> { if (currentState == State.GAME) tryMove(GameManager.Direction.DOWN);  });
        onKeyDown(javafx.scene.input.KeyCode.A, () -> { if (currentState == State.GAME) tryMove(GameManager.Direction.LEFT);  });
        onKeyDown(javafx.scene.input.KeyCode.D, () -> { if (currentState == State.GAME) tryMove(GameManager.Direction.RIGHT); });
        onKeyDown(javafx.scene.input.KeyCode.E, () -> { if (currentState == State.GAME) tryEndTurn();            });
        onKeyDown(javafx.scene.input.KeyCode.H, () -> { if (currentState == State.GAME) trySave();               });
        onKeyDown(javafx.scene.input.KeyCode.L, () -> { if (currentState == State.GAME) tryLoad();               });
    }

    // ------------------ صفحات پیش از بازی ------------------

    private void showWelcomeScreen() {
        currentState = State.WELCOME;

        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);

        Text welcome = new Text("🎮 به بازی RoyalWar خوش آمدید!");
        welcome.setFill(Color.WHITE);
        welcome.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Text sub = new Text("لطفاً ثبت‌نام کنید یا وارد شوید تا به لابی بروید");
        sub.setFill(Color.web(TXT_MUTED));

        Button btnLogin    = makeButton("ورود",    BTN_BLUE,  this::showLoginForm);
        Button btnRegister = makeButton("ثبت‌نام", BTN_GREEN, this::showRegisterForm);

        box.getChildren().addAll(welcome, sub, btnLogin, btnRegister);
        setScreen(box);
    }

    // ثبت‌نام: ذخیره کاربر و بازگشت به خوش‌آمدگویی (طبق سناریو)
    private void showRegisterForm() {
        currentState = State.REGISTER;

        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));

        Text title = new Text("📋 ثبت‌نام");
        title.setFill(Color.WHITE);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField tfUser = new TextField();    tfUser.setPromptText("نام کاربری");
        PasswordField pfPass = new PasswordField(); pfPass.setPromptText("رمز عبور");

        Button btnSubmit = makeButton("ثبت اطلاعات", BTN_GREEN, () -> {
            String u = tfUser.getText();
            String p = pfPass.getText();
            if (userManager.register(u, p)) {
                FXGL.getDialogService().showMessageBox("✅ ثبت‌نام موفق! اکنون از بخش ورود وارد شوید.", this::showWelcomeScreen);
            } else {
                FXGL.getDialogService().showMessageBox("❌ نام کاربری تکراری یا نامعتبر.");
            }
        });

        Button btnBack = makeButton("بازگشت", BTN_AMBER, this::showWelcomeScreen);

        box.getChildren().addAll(title, tfUser, pfPass, btnSubmit, btnBack);
        setScreen(box);
    }

    // ورود: افزودن به لابی، تنظیم currentUsername و رفتن به صفحه لابی
    private void showLoginForm() {
        currentState = State.LOGIN;

        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));

        Text title = new Text("🔐 ورود");
        title.setFill(Color.WHITE);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField tfUser = new TextField();    tfUser.setPromptText("نام کاربری");
        PasswordField pfPass = new PasswordField(); pfPass.setPromptText("رمز عبور");

        Button btnLogin = makeButton("ورود", BTN_BLUE, () -> {
            String u = tfUser.getText();
            String p = pfPass.getText();
            if (userManager.login(u, p)) {
                currentUsername = u;
                if (!lobby.isInLobby(u)) {
                    lobby.addPlayer(u);
                    lobbyIO.save(lobby);
                }
                FXGL.getDialogService().showMessageBox("✅ ورود موفق! به لابی منتقل می‌شوید.", this::showLobbyScreen);
            } else {
                FXGL.getDialogService().showMessageBox("❌ نام کاربری یا رمز عبور اشتباه.");
            }
        });

        Button btnBack = makeButton("بازگشت", BTN_AMBER, this::showWelcomeScreen);

        box.getChildren().addAll(title, tfUser, pfPass, btnLogin, btnBack);
        setScreen(box);
    }

    // لابی: اسلات‌ها + ثبت‌نام/ورود بازیکن جدید + خروج کاربر فعلی + شروع بازی
    private void showLobbyScreen() {
        currentState = State.LOBBY;

        VBox box = new VBox(16);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(24));

        Text title = new Text("🏰 لابی بازی");
        title.setFill(Color.WHITE);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Text currentUserText = new Text("کاربر جاری: " + (currentUsername != null ? currentUsername : "-"));
        currentUserText.setFill(Color.web(TXT_MUTED));

        VBox slotsBox = new VBox(8);
        slotsBox.setAlignment(Pos.CENTER);

        for (int i = 0; i < 4; i++) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER);

            final String slotName = lobby.getSlotName(i);
            Text t = new Text("اسلات " + (i + 1) + ": " + (slotName != null ? slotName : "خالی"));
            t.setFill(slotName != null ? Color.LIGHTGREEN : Color.web(TXT_MUTED));
            t.setStyle("-fx-font-size: 14px;");

            Button btnRemove = makeButton("خروج از اسلات", BTN_AMBER, () -> {
                if (slotName != null) {
                    lobby.removePlayer(slotName);
                    lobbyIO.save(lobby);
                    showLobbyScreen();
                }
            });
            btnRemove.setDisable(slotName == null);

            row.getChildren().addAll(t, btnRemove);
            slotsBox.getChildren().add(row);
        }

        int count = lobby.getPlayerCount();
        boolean canStart = lobby.canStartGame();

        Text statusBar = new Text("بازیکنان: " + count + "/4    وضعیت شروع: " + (canStart ? "فعال" : "غیرفعال"));
        statusBar.setFill(canStart ? Color.LIGHTGREEN : Color.web(TXT_MUTED));
        statusBar.setStyle("-fx-font-size: 14px;");

        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER);

        Button btnRegisterNew = makeButton("ثبت‌نام بازیکن جدید", BTN_GREEN, this::showRegisterForm);
        Button btnLoginNew    = makeButton("ورود بازیکن جدید",   BTN_BLUE,  this::showLoginForm);

        Button btnExitUser = makeButton("خروج از لابی", BTN_AMBER, () -> {
            if (currentUsername != null) {
                if (lobby.isInLobby(currentUsername)) {
                    lobby.removePlayer(currentUsername);
                    lobbyIO.save(lobby);
                }
                currentUsername = null;
                showWelcomeScreen();
            }
        });
        btnExitUser.setDisable(currentUsername == null);

        controls.getChildren().addAll(btnRegisterNew, btnLoginNew, btnExitUser);

        Button btnStart = makeButton("شروع بازی", BTN_AMBER, () -> {
            if (!lobby.canStartGame()) {
                FXGL.getDialogService().showMessageBox("❌ حداقل دو بازیکن لازم است.");
                return;
            }
            List<String> names = new ArrayList<>();
            for (String s : lobby.getAll()) if (s != null) names.add(s);
            startGameWithPlayers(names);
        });
        btnStart.setDisable(!canStart);

        box.getChildren().addAll(title, currentUserText, slotsBox, statusBar, controls, btnStart);
        setScreen(box);
    }

    // ------------------ شروع بازی و لایه‌ها ------------------

    private void startGameWithPlayers(List<String> names) {
        currentState = State.GAME;
        getGameScene().clearUINodes();

        // پاک‌سازی لایه‌ها
        playerTokens.clear();
        playersGroup = new Group();
        markersGroup = new Group();

        // ساخت بازی
        gm = new GameManager(names);
        // حذف خطای addUIListener با استفاده از رابط مناسب
        if (gm instanceof GameEventNotifier) {
            ((GameEventNotifier) gm).addUIListener(new GameUIListener());
        }

        // رندر لایه‌ها
        renderMap();       // layer 0
        renderMarkers();   // layer 1
        renderPlayers();   // layer 2

        // HUD و پنل‌ها
        initHUD();
        initResourcesPanel();
        initControlsPanel();

        // مقداردهی اولیه
        updateHUD();
        updateResourcesPanel();
    }

    // ------------------ HUD ------------------

    private void initHUD() {
        hudTurnText = new Text();   hudTurnText.setFill(Color.WHITE);
        hudTurnText.setTranslateX(8);
        hudTurnText.setTranslateY(GameConfig.MAP_SIZE * TILE + 20);

        hudRoundText = new Text();  hudRoundText.setFill(Color.WHITE);
        hudRoundText.setTranslateX(200);
        hudRoundText.setTranslateY(GameConfig.MAP_SIZE * TILE + 20);

        hudInfoText = new Text("راهنمای کنترل‌ها در پایین راست صفحه");
        hudInfoText.setFill(Color.web("#93c5fd"));
        hudInfoText.setTranslateX(8);
        hudInfoText.setTranslateY(GameConfig.MAP_SIZE * TILE + 44);

        addUINode(hudTurnText);
        addUINode(hudRoundText);
        addUINode(hudInfoText);
    }

    private void updateHUD() {
        Player cur = gm.getCurrentPlayer();
        hudTurnText.setText("نوبت: " + cur.getName());
        hudRoundText.setText("دور: " + gm.getTurnManager().getCurrentIndex());
    }

    // ------------------ پنل منابع ------------------

    private void initResourcesPanel() {
        resourcesPanel = new VBox(6);
        resourcesPanel.setPadding(new Insets(10));
        resourcesPanel.setStyle(
                "-fx-background-color: rgba(31,41,55,0.85);" +
                        "-fx-border-color: white; -fx-border-width: 1px;"
        );
        resourcesPanel.setTranslateX(getAppWidth() - 280);
        resourcesPanel.setTranslateY(16);
        addUINode(resourcesPanel);
    }

    private void updateResourcesPanel() {
        resourcesPanel.getChildren().clear();

        Text title = new Text("منابع");
        title.setFill(Color.WHITE);
        title.setStyle("-fx-font-weight: bold;");
        resourcesPanel.getChildren().add(title);

        for (Player p : gm.getPlayers()) {
            Map<String, Integer> res = readResourcesForPlayer(p);
            String line = p.getName();
            if (!res.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                boolean first = true;
                for (Map.Entry<String, Integer> e : res.entrySet()) {
                    if (!first) sb.append(" | ");
                    sb.append(e.getKey()).append(": ").append(e.getValue());
                    first = false;
                }
                line += " — " + sb;
            }
            Text t = new Text(line);
            t.setFill(p.equals(gm.getCurrentPlayer()) ? Color.YELLOW : Color.WHITE);
            resourcesPanel.getChildren().add(t);
        }
    }

    // تلاش ایمن: خواندن منابع از Player یا Economy (اگر موجود باشند). در غیر این صورت خالی.
    private Map<String, Integer> readResourcesForPlayer(Player p) {
        Map<String, Integer> m = new LinkedHashMap<>();
        tryGetInt(p, "getGold").ifPresent(v -> m.put("طلا", v));
        tryGetInt(p, "getFood").ifPresent(v -> m.put("غذا", v));
        tryGetInt(p, "getWood").ifPresent(v -> m.put("چوب", v));
        tryGetInt(p, "getStone").ifPresent(v -> m.put("سنگ", v));
        if (!m.isEmpty()) return m;

        try {
            Object eco = tryGet(gm, "getEconomy").orElse(null);
            if (eco != null) {
                Integer gold = tryGetInt(eco, "getGold", int.class, p.getId()).orElse(null);
                Integer food = tryGetInt(eco, "getFood", int.class, p.getId()).orElse(null);
                Integer wood = tryGetInt(eco, "getWood", int.class, p.getId()).orElse(null);
                Integer stone= tryGetInt(eco, "getStone",int.class, p.getId()).orElse(null);
                if (gold != null) m.put("طلا", gold);
                if (food != null) m.put("غذا", food);
                if (wood != null) m.put("چوب", wood);
                if (stone!= null) m.put("سنگ", stone);
            }
        } catch (Exception ignore) {}

        // اگر هنوز هیچ منبعی پیدا نشده، مقادیر پیش‌فرض قرار دهید
        if (m.isEmpty()) {
            m.put("طلا", 100);
            m.put("غذا", 100);
            m.put("چوب", 50);
            m.put("سنگ", 50);
        }

        return m;
    }

    private Optional<Integer> tryGetInt(Object obj, String methodName) {
        try {
            Method m = obj.getClass().getMethod(methodName);
            Object res = m.invoke(obj);
            if (res instanceof Number) return Optional.of(((Number) res).intValue());
        } catch (Exception ignored) {}
        return Optional.empty();
    }

    private Optional<Integer> tryGetInt(Object obj, String methodName, Class<?> paramType, Object arg) {
        try {
            Method m = obj.getClass().getMethod(methodName, paramType);
            Object res = m.invoke(obj, arg);
            if (res instanceof Number) return Optional.of(((Number) res).intValue());
        } catch (Exception ignored) {}
        return Optional.empty();
    }

    private Optional<Object> tryGet(Object obj, String methodName) {
        try {
            Method m = obj.getClass().getMethod(methodName);
            return Optional.ofNullable(m.invoke(obj));
        } catch (Exception ignored) {}
        return Optional.empty();
    }

    // ------------------ پنل راهنمای کنترل‌ها ------------------

    private void initControlsPanel() {
        controlsPanel = new VBox(6);
        controlsPanel.setPadding(new Insets(10));
        controlsPanel.setStyle(
                "-fx-background-color: rgba(31,41,55,0.85);" +
                        "-fx-border-color: white; -fx-border-width: 1px;"
        );
        controlsPanel.setTranslateX(getAppWidth() - 280);
        controlsPanel.setTranslateY(getAppHeight() - 160);

        Text title = new Text("کنترل‌ها");
        title.setFill(Color.WHITE);
        title.setStyle("-fx-font-weight: bold;");
        Text t1 = new Text("W/A/S/D: حرکت");
        t1.setFill(Color.LIGHTGRAY);
        Text t2 = new Text("E: پایان نوبت");
        t2.setFill(Color.LIGHTGRAY);
        Text t3 = new Text("H/L: ذخیره/بارگذاری");
        t3.setFill(Color.LIGHTGRAY);

        controlsPanel.getChildren().addAll(title, t1, t2, t3);
        addUINode(controlsPanel);
    }

    // ------------------ رندر نقشه، نشانه‌ها، بازیکنان ------------------

    private void renderMap() {
        if (mapView != null) {
            getGameScene().removeGameView(mapView);
        }

        Group g = new Group();
        GameMap map = gm.getMap();
        for (int y = 0; y < map.getSize(); y++) {
            for (int x = 0; x < map.getSize(); x++) {
                Cell cell = map.getCell(x, y);
                Rectangle r = new Rectangle(TILE - 1, TILE - 1);
                r.setTranslateX(x * TILE);
                r.setTranslateY(y * TILE);
                r.setFill(colorFor(cell));
                g.getChildren().add(r);
            }
        }
        mapView = new GameView(g, 0);
        getGameScene().addGameView(mapView);
    }

    private void renderMarkers() {
        markersGroup.getChildren().clear();
        if (markersView != null) {
            getGameScene().removeGameView(markersView);
        }

        GameMap map = gm.getMap();
        for (int y = 0; y < map.getSize(); y++) {
            for (int x = 0; x < map.getSize(); x++) {
                Cell cell = map.getCell(x, y);

                String type = cell.getType().toString();
                if ("MONSTER_STRONGHOLD".equals(type)) {
                    // نمایش هیولا با یک شکل خاص
                    Polygon monster = new Polygon(
                            0, -8,
                            6, 0,
                            8, 6,
                            0, 8,
                            -8, 6,
                            -6, 0
                    );
                    monster.setFill(Color.web("#ef4444"));
                    monster.setStroke(Color.BLACK);
                    moveToCellCenter(monster, x, y);
                    markersGroup.getChildren().add(monster);

                    // اضافه کردن متن "هیولا"
                    Text monsterText = new Text("هیولا");
                    monsterText.setFill(Color.WHITE);
                    monsterText.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
                    moveToCellCenter(monsterText, x, y);
                    monsterText.setTranslateY(monsterText.getTranslateY() + 20);
                    markersGroup.getChildren().add(monsterText);
                } else if ("PLAYER_CASTLE".equals(type) && cell.getOwnerId() <= 0) {
                    // نمایش قلعه‌های خالی
                    Rectangle castle = new Rectangle(12, 12, Color.TRANSPARENT);
                    castle.setStroke(Color.WHITE);
                    castle.setStrokeWidth(1.5);
                    moveToCellCenter(castle, x, y);
                    markersGroup.getChildren().add(castle);

                    // اضافه کردن متن "قلعه"
                    Text castleText = new Text("قلعه");
                    castleText.setFill(Color.WHITE);
                    castleText.setStyle("-fx-font-size: 10px;");
                    moveToCellCenter(castleText, x, y);
                    castleText.setTranslateY(castleText.getTranslateY() + 20);
                    markersGroup.getChildren().add(castleText);
                }
            }
        }
        markersView = new GameView(markersGroup, 1);
        getGameScene().addGameView(markersView);
    }

    private void renderPlayers() {
        playersGroup.getChildren().clear();
        if (playersView != null) {
            getGameScene().removeGameView(playersView);
        }
        playerTokens.clear();

        // رنگ‌های مختلف برای بازیکنان
        Color[] playerColors = {
                Color.web("#2563eb"), // آبی
                Color.web("#16a34a"), // سبز
                Color.web("#d97706"), // نارنجی
                Color.web("#a855f7")  // بنفش
        };

        for (int i = 0; i < gm.getPlayers().size(); i++) {
            Player p = gm.getPlayers().get(i);
            Circle c = new Circle(TILE * 0.4, playerColors[i]);
            c.setStroke(Color.WHITE);
            c.setStrokeWidth(2);
            setNodeToGrid(c, p.getPosition());

            // اضافه کردن متن نام بازیکن
            Text nameText = new Text(p.getName());
            nameText.setFill(Color.WHITE);
            nameText.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
            setNodeToGrid(nameText, p.getPosition());
            nameText.setTranslateY(nameText.getTranslateY() + 20);

            playersGroup.getChildren().addAll(c, nameText);
            playerTokens.put(p.getId(), c);
        }
        playersView = new GameView(playersGroup, 2);
        getGameScene().addGameView(playersView);
    }

    private int getPlayerIndex(Player p) {
        int idx = 0;
        for (Player q : gm.getPlayers()) {
            if (q.getId() == p.getId()) return idx;
            idx++;
        }
        return 0;
    }

    private void movePlayerNode(Player p) {
        Node n = playerTokens.get(p.getId());
        if (n == null) return;
        setNodeToGrid(n, p.getPosition());
    }

    // ------------------ رنگ‌بندی سلول ------------------

    private Color colorFor(Cell cell) {
        String type = cell.getType().toString();
        if ("OBSTACLE".equals(type))            return Color.web("#4b5563");
        if ("MONSTER_STRONGHOLD".equals(type))  return Color.web("#7f1d1d");
        if ("PLAYER_CASTLE".equals(type)) {
            if (cell.getOwnerId() <= 0) return Color.web("#374151"); // قلعه آزاد
            switch (cell.getOwnerId()) {
                case 1: return Color.web("#1e40af");
                case 2: return Color.web("#166534");
                case 3: return Color.web("#92400e");
                case 4: return Color.web("#7e22ce");
                default: return Color.web("#1f2937");
            }
        }
        // EMPTY و سایر حالت‌ها
        return Color.web("#111827");
    }

    // ------------------ عملیات بازی ------------------

    private void tryMove(GameManager.Direction dir) {
        try {
            gm.moveCurrentPlayer(dir);
            // به روزرسانی فوری رابط کاربری
            updateHUD();
            updateResourcesPanel();
        } catch (Exception e) {
            FXGL.getNotificationService().pushNotification("⚠ " + e.getMessage());
        }
    }

    private void tryEndTurn() {
        try {
            gm.endTurn();
            // به روزرسانی فوری رابط کاربری
            updateHUD();
            updateResourcesPanel();
        } catch (Exception e) {
            FXGL.getNotificationService().pushNotification("⚠ " + e.getMessage());
        }
    }

    private void trySave() {
        try {
            SaveLoadManager.save(gm);
            FXGL.getNotificationService().pushNotification("💾 ذخیره شد");
        } catch (Exception e) {
            FXGL.getNotificationService().pushNotification("❌ ذخیره با خطا مواجه شد");
        }
    }

    private void tryLoad() {
        try {
            GameManager loaded = SaveLoadManager.load();
            this.gm = loaded;
            // حذف خطای addUIListener با استفاده از رابط مناسب
            if (gm instanceof GameEventNotifier) {
                ((GameEventNotifier) gm).addUIListener(new GameUIListener());
            }
            renderMap();
            renderMarkers();
            renderPlayers();
            updateHUD();
            updateResourcesPanel();
            FXGL.getNotificationService().pushNotification("📂 بارگذاری شد");
        } catch (Exception e) {
            FXGL.getNotificationService().pushNotification("❌ بارگذاری با خطا مواجه شد");
        }
    }

    // ------------------ ابزار مختص مختصات ------------------

    private void setNodeToGrid(Node n, Position pos) {
        n.setTranslateX(pos.getX() * TILE + TILE * 0.5);
        n.setTranslateY(pos.getY() * TILE + TILE * 0.5);
    }

    private void moveToCellCenter(Node n, int x, int y) {
        n.setTranslateX(x * TILE + TILE * 0.5);
        n.setTranslateY(y * TILE + TILE * 0.5);
    }

    // ------------------ Hookهای UI ------------------

    // ایجاد یک کلاس جداگانه برای مدیریت رویدادهای UI
    private class GameUIListener implements GameEventListener {
        @Override
        public void onPlayerMoved(Player player, Position newPos) {
            Platform.runLater(() -> {
                movePlayerNode(player);
                updateHUD();
                updateResourcesPanel();
            });
        }

        @Override
        public void onTurnEnded(Player prevPlayer, int nextPlayerId, int round) {
            Platform.runLater(() -> {
                updateHUD();
                updateResourcesPanel();
            });
        }

        @Override
        public void onCastleCaptured(Castle castle, Player newOwner) {
            Platform.runLater(() -> {
                renderMap();
                renderMarkers();
                updateResourcesPanel();
                FXGL.getNotificationService().pushNotification(
                        "🏰 قلعه توسط " + newOwner.getName() + " تصرف شد"
                );
            });
        }

        @Override
        public void onEventTriggered(Event event) {
            Platform.runLater(() ->
                    FXGL.getNotificationService().pushNotification("🌀 رویداد: " + event.getName())
            );
        }

        @Override
        public void onBattleStarted(Attack attack) {
            Platform.runLater(() ->
                    FXGL.getNotificationService().pushNotification("⚔ نبرد: " + attack.getType())
            );
        }

        @Override
        public void onBattleResolved(Attack attack) {
            Platform.runLater(() ->
                    FXGL.getNotificationService().pushNotification("✅ نبرد پایان یافت")
            );
        }

        @Override
        public void onGameEnded(Player winner, String reason) {
            Platform.runLater(() ->
                    FXGL.getDialogService().showMessageBox(
                            "🏆 برنده: " + winner.getName() + "\nدلیل: " + reason,
                            () -> showWelcomeScreen()
                    )
            );
        }

        @Override
        public void onResourceChanged(Player player, String resource, int newValue) {
            Platform.runLater(() -> {
                updateResourcesPanel();
            });
        }
    }

    // ------------------ ابزارهای کمکی UI ------------------

    private Button makeButton(String text, String bgColor, Runnable action) {
        Button b = new Button(text);
        b.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;"
        );
        b.setOnAction(e -> action.run());
        b.setPrefWidth(220);
        return b;
    }

    private void setScreen(Node content) {
        getGameScene().clearUINodes();
        StackPane root = new StackPane(content);
        root.setStyle("-fx-background-color: " + BG_DARK + ";");
        root.setPrefSize(getAppWidth(), getAppHeight());
        StackPane.setAlignment(content, Pos.CENTER);
        addUINode(root);
    }

    public static void main(String[] args) {
        launch(args);
    }

    // ------------------ کلاس‌های کمکی برای حل مشکلات ------------------

    // اضافه کردن اینترفیس GameEventListener برای حل مشکل کامپایل
    public interface GameEventListener {
        void onPlayerMoved(Player player, Position newPos);
        void onTurnEnded(Player prevPlayer, int nextPlayerId, int round);
        void onCastleCaptured(Castle castle, Player newOwner);
        void onEventTriggered(Event event);
        void onBattleStarted(Attack attack);
        void onBattleResolved(Attack attack);
        void onGameEnded(Player winner, String reason);
        void onResourceChanged(Player player, String resource, int newValue);
    }

    // اضافه کردن اینترفیس برای اطلاع‌رسانی رویدادهای بازی
    public interface GameEventNotifier {
        void addUIListener(GameEventListener listener);
    }

    // اضافه کردن کلاس‌های ضروری برای حل مشکلات کامپایل
    public static class GameConfig {
        public static final int MAP_SIZE = 16;
    }

    // ======= Minimal stub model to compile standalone =======

    public static class Position {
        private int x, y;
        public Position(int x, int y) { this.x = x; this.y = y; }
        public int getX() { return x; }
        public int getY() { return y; }
        public void set(int x, int y) { this.x = x; this.y = y; }
    }

    public static class Player {
        private final int id;
        private final String name;
        private Position position;
        private final Map<String, Integer> resources = new LinkedHashMap<>();
        public Player(int id, String name, Position pos) {
            this.id = id;
            this.name = name;
            this.position = pos;
            // منابع اولیه پیش‌فرض
            resources.put("طلا", 100);
            resources.put("غذا", 100);
            resources.put("چوب", 50);
            resources.put("سنگ", 50);
        }
        public int getId() { return id; }
        public String getName() { return name; }
        public Position getPosition() { return position; }
        public void setPosition(Position p) { this.position = p; }

        // متدهای کمکی برای پنل منابع
        public int getGold()  { return resources.getOrDefault("طلا", 0); }
        public int getFood()  { return resources.getOrDefault("غذا", 0); }
        public int getWood()  { return resources.getOrDefault("چوب", 0); }
        public int getStone() { return resources.getOrDefault("سنگ", 0); }
        public Map<String, Integer> getResources() { return resources; }
    }

    public enum CellType { EMPTY, OBSTACLE, MONSTER_STRONGHOLD, PLAYER_CASTLE }

    public static class Cell {
        private final CellType type;
        private int ownerId = 0; // 0 یعنی آزاد
        public Cell(CellType type) { this.type = type; }
        public CellType getType() { return type; }
        public int getOwnerId() { return ownerId; }
        public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
    }

    public static class GameMap {
        private final int size;
        private final Cell[][] grid;
        public GameMap(int size, int playerCount) {
            this.size = size;
            this.grid = new Cell[size][size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    grid[y][x] = new Cell(CellType.EMPTY);
                }
            }
            // یک دژ هیولا در مرکز
            int c = size / 2;
            grid[c][c] = new Cell(CellType.MONSTER_STRONGHOLD);

            // چند مانع ساده
            for (int i = 2; i < size - 2; i += 4) {
                grid[2][i] = new Cell(CellType.OBSTACLE);
                grid[size - 3][i] = new Cell(CellType.OBSTACLE);
            }

            // چند قلعه بازیکن آزاد (بدون مالک) در گوشه‌ها
            int[][] castles = {
                    {0, 0}, {size - 1, 0}, {0, size - 1}, {size - 1, size - 1}
            };
            for (int i = 0; i < Math.min(playerCount, castles.length); i++) {
                int x = castles[i][0], y = castles[i][1];
                grid[y][x] = new Cell(CellType.PLAYER_CASTLE);
                grid[y][x].setOwnerId(0);
            }
        }
        public int getSize() { return size; }
        public Cell getCell(int x, int y) { return grid[y][x]; }
    }

    public static class Castle {
        private final Position position;
        public Castle(Position position) { this.position = position; }
        public Position getPosition() { return position; }
    }

    public static class TurnManager {
        private int turnNumber = 1;
        private int currentIndex = 0;
        public int getTurnNumber() { return turnNumber; }
        public int getCurrentIndex() { return currentIndex; }
        private void next(int players) {
            currentIndex = (currentIndex + 1) % players;
            if (currentIndex == 0) turnNumber++;
        }
    }

    public static class GameManager implements GameEventNotifier {
        public enum Direction { UP, DOWN, LEFT, RIGHT }

        private final List<Player> players;
        private final TurnManager turnManager = new TurnManager();
        private final GameMap map;
        private final List<GameEventListener> listeners = new ArrayList<>();

        public GameManager(List<String> names) {
            int size = GameConfig.MAP_SIZE;
            this.map = new GameMap(size, names.size());
            this.players = new ArrayList<>();
            int[][] spawns = {
                    {0, 0}, {size - 1, 0}, {0, size - 1}, {size - 1, size - 1}
            };
            int i = 0;
            for (String n : names) {
                int x = spawns[i % 4][0], y = spawns[i % 4][1];
                players.add(new Player(i + 1, n, new Position(x, y)));
                i++;
            }
        }

        public List<Player> getPlayers() { return players; }
        public Player getCurrentPlayer() { return players.get(turnManager.getCurrentIndex()); }
        public TurnManager getTurnManager() { return turnManager; }
        public GameMap getMap() { return map; }

        public void moveCurrentPlayer(Direction dir) {
            Player p = getCurrentPlayer();
            int x = p.getPosition().getX();
            int y = p.getPosition().getY();
            switch (dir) {
                case UP:    y = Math.max(0, y - 1); break;
                case DOWN:  y = Math.min(map.getSize() - 1, y + 1); break;
                case LEFT:  x = Math.max(0, x - 1); break;
                case RIGHT: x = Math.min(map.getSize() - 1, x + 1); break;
            }
            Cell cell = map.getCell(x, y);
            if (cell.getType() == CellType.OBSTACLE) {
                // برخورد با مانع: حرکت انجام نشود
                return;
            }
            p.setPosition(new Position(x, y));
            if (cell.getType() == CellType.PLAYER_CASTLE && cell.getOwnerId() <= 0) {
                cell.setOwnerId(p.getId());
                Castle castle = new Castle(new Position(x, y));
                for (GameEventListener l : listeners) l.onCastleCaptured(castle, p);
            }
        }

        public void endTurn() {
            Player prev = getCurrentPlayer();
            turnManager.next(players.size());
            int nextId = getCurrentPlayer().getId();
            for (GameEventListener l : listeners) l.onTurnEnded(prev, nextId, turnManager.getTurnNumber());
        }

        @Override
        public void addUIListener(GameEventListener listener) {
            listeners.add(listener);
        }
    }

    public static class SaveLoadManager {
        private static GameManager last;
        public static void save(GameManager gm) { last = gm; }
        public static GameManager load() {
            if (last == null) throw new RuntimeException("هیچ ذخیره‌ای وجود ندارد");
            return last;
        }
    }

    public static class UserManager {
        private final Map<String, String> users = new HashMap<>();
        public boolean register(String u, String p) {
            if (u == null || u.isBlank() || p == null || p.isBlank()) return false;
            if (users.containsKey(u)) return false;
            users.put(u, p);
            return true;
        }
        public boolean login(String u, String p) {
            return users.containsKey(u) && Objects.equals(users.get(u), p);
        }
    }

    public static class Lobby {
        private final String[] slots = new String[4];
        public boolean isInLobby(String u) {
            if (u == null) return false;
            for (String s : slots) if (u.equals(s)) return true;
            return false;
        }
        public boolean addPlayer(String u) {
            if (u == null || u.isBlank() || isInLobby(u)) return false;
            for (int i = 0; i < slots.length; i++) {
                if (slots[i] == null) { slots[i] = u; return true; }
            }
            return false;
        }
        public String getSlotName(int i) { return (i >= 0 && i < slots.length) ? slots[i] : null; }
        public void removePlayer(String name) {
            for (int i = 0; i < slots.length; i++) {
                if (Objects.equals(slots[i], name)) slots[i] = null;
            }
        }
        public int getPlayerCount() {
            int c = 0; for (String s : slots) if (s != null) c++; return c;
        }
        public boolean canStartGame() { return getPlayerCount() >= 2; }
        public List<String> getAll() { return Arrays.asList(slots); }
    }

    public static class LobbyPersistence {
        public void save(Lobby l) { /* no-op for this standalone file */ }
    }

}
