import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class RoyalWarLobbyApp extends GameApplication {

    private UserManager userManager = new UserManager();
    private Lobby lobby = new Lobby();
    private LobbyPersistence lobbyIO = new LobbyPersistence();

    private StackPane rootUI = new StackPane();

    private Label[] slotLabels = new Label[4];
    private Button[] removeButtons = new Button[4];
    private Button startBtn;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("RoyalWar");
        settings.setIntroEnabled(false);
        settings.setMainMenuEnabled(false);
    }

    @Override
    protected void initUI() {
        lobbyIO.load(lobby);
        showView(createLobbyView());
        FXGL.addUINode(rootUI, 0, 0);
    }

    // ---------- Lobby View ----------
    private VBox buildSlotsBar() {
        VBox slotsBar = new VBox(8);
        for (int i = 0; i < 4; i++) {
            final int index = i;

            HBox row = new HBox(10);
            Label lbl = new Label("Slot " + (i + 1) + ": Empty");
            slotLabels[i] = lbl;

            Button btnExit = new Button("Exit");
            removeButtons[i] = btnExit;

            btnExit.setOnAction(actionEvent -> {
                String name = lobby.getSlotName(index);
                if (name != null) {
                    lobby.removePlayer(name);
                    lobbyIO.save(lobby);
                    updateLobbyUI();
                    FXGL.getDialogService().showMessageBox("Player \"" + name + "\" left from the game!");
                }
            });

            row.getChildren().addAll(lbl, btnExit);
            slotsBar.getChildren().add(row);
        }
        return slotsBar;
    }

    private Node createLobbyView() {
        VBox box = new VBox(14);
        box.setPadding(new Insets(20));

        Text title = new Text("Game lobby");
        box.getChildren().add(title);

        VBox slotsBar = buildSlotsBar();
        box.getChildren().add(slotsBar);

        HBox buttons = new HBox(10);

        Button btnLogin = new Button("Login");
        btnLogin.setOnAction(actionEvent -> showView(createLoginView()));

        Button btnRegister = new Button("Register");
        btnRegister.setOnAction(actionEvent -> showView(createRegisterView()));

        startBtn = new Button("Start Game");
        startBtn.setDisable(!lobby.canStartGame());
        startBtn.setOnAction(actionEvent -> FXGL.getDialogService().showMessageBox("بازی شروع شد! (نمایش صرفاً جهت تست فاز گرافیکی)"));

        buttons.getChildren().addAll(btnLogin, btnRegister, startBtn);
        box.getChildren().add(buttons);

        BorderPane frame = new BorderPane();
        frame.setCenter(box);
        frame.setPadding(new Insets(10));
        frame.setStyle("-fx-background-color: #2b2b2b; -fx-text-fill: white;");

        updateLobbyUI();
        return frame;
    }

    // ---------- Register View ----------
    private Node createRegisterView() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        box.getChildren().add(new Text("Register"));

        TextField tfUser = new TextField();
        tfUser.setPromptText("Username");

        PasswordField pfPass = new PasswordField();
        pfPass.setPromptText("Password");

        HBox btns = new HBox(10);
        Button btnSubmit = new Button("Register");
        Button btnBack = new Button("Return");

        btnSubmit.setOnAction(actionEvent -> {
            String u = tfUser.getText();
            String p = pfPass.getText();

            boolean ok = userManager.register(u, p);
            if (ok) {
                FXGL.getDialogService().showMessageBox("Your registration was successful!");
                goBackToLobby();
            } else {
                FXGL.getDialogService().showMessageBox("Duplicate username or invalid input.");
            }
        });

        btnBack.setOnAction(actionEvent -> goBackToLobby());

        btns.getChildren().addAll(btnSubmit, btnBack);
        box.getChildren().addAll(tfUser, pfPass, btns);

        BorderPane frame = new BorderPane();
        frame.setCenter(box);
        frame.setPadding(new Insets(10));
        frame.setStyle("-fx-background-color: #2b2b2b; -fx-text-fill: white;");
        return frame;
    }

    // ---------- Login View ----------
    private Node createLoginView() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        box.getChildren().add(new Text("Login"));

        TextField tfUser = new TextField();
        tfUser.setPromptText("Username");

        PasswordField pfPass = new PasswordField();
        pfPass.setPromptText("Password");

        HBox btns = new HBox(10);
        Button btnSubmit = new Button("Login");
        Button btnBack = new Button("Return");

        btnSubmit.setOnAction(actionEvent -> {
            String u = tfUser.getText();
            String p = pfPass.getText();

            boolean ok = userManager.login(u, p);
            if (ok) {
                boolean added = lobby.addPlayer(u);
                if (added) {
                    lobbyIO.save(lobby);
                    FXGL.getDialogService().showMessageBox("Your login was successful!");
                    goBackToLobby();
                } else {
                    FXGL.getDialogService().showMessageBox("The lobby is full!");
                }
            } else {
                FXGL.getDialogService().showMessageBox("The username or password is incorrect! Try again.");
            }
        });

        btnBack.setOnAction(actionEvent -> goBackToLobby());

        btns.getChildren().addAll(btnSubmit, btnBack);
        box.getChildren().addAll(tfUser, pfPass, btns);

        BorderPane frame = new BorderPane();
        frame.setCenter(box);
        frame.setPadding(new Insets(10));
        frame.setStyle("-fx-background-color: #2b2b2b; -fx-text-fill: white;");
        return frame;
    }

    // ---------- Helpers ----------
    private void showView(Node view) {
        rootUI.getChildren().clear();
        rootUI.getChildren().add(view);
    }

    private void goBackToLobby() {
        showView(createLobbyView());
    }

    private void updateLobbyUI() {
        for (int i = 0; i < 4; i++) {
            String name = lobby.getSlotName(i);
            if (slotLabels[i] != null) {
                slotLabels[i].setText("Slot " + (i + 1) + ": " + (name != null ? name : "Empty"));
            }
            if (removeButtons[i] != null) {
                removeButtons[i].setDisable(name == null);
            }
        }
        if (startBtn != null) {
            startBtn.setDisable(!lobby.canStartGame());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}