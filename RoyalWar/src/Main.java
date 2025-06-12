import com.almasb.fxgl.app.*;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.*;
import com.almasb.fxgl.physics.CollisionHandler;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class Main extends GameApplication {

    //options of game
    public static final int WIDTH=1280, HEIGHT=720;
    private static final int SPEED = 2;
    private static final float FONTGAME = 25.0f;
    private static final int NUM_STARS = 2000;
    private static final double STAR_SPEED = 0.02;

    //gradient
    Stop[] stops = new Stop[] {
            new Stop(0, Color.rgb(0,0,64)),
            new Stop(1, Color.BLACK)
    };
    LinearGradient gradient = new LinearGradient(
            0, 0, 0, 1, // جهت عمودی
            true, // proportional
            CycleMethod.NO_CYCLE,
            stops
    );

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setTitle("Royal War");
        settings.setAppIcon("app.png");
        settings.setVersion("0.1");
        settings.setFullScreenAllowed(true);
        //settings.setFullScreenFromStart(true);
        settings.setMainMenuEnabled(true);
    }

    public enum EntityType {
        PLAYER1, PLAYER2, MONSTER
    }
    public Entity player1, player2, monster;

    @Override
    protected void initGame() {
        //players
        player1 = FXGL.entityBuilder()
                .type(EntityType.PLAYER1)
                .at(100, 150)
                .viewWithBBox("player1.png")
                .with(new CollidableComponent(true))
                .buildAndAttach();
        player2 = FXGL.entityBuilder()
                .type(EntityType.PLAYER2)
                .at(840, 150)
                .viewWithBBox("player2.png")
                .with(new CollidableComponent(true))
                .buildAndAttach();

        //monster
        monster = FXGL.entityBuilder()
                .type(EntityType.MONSTER)
                .at(530, 190)
                .viewWithBBox("monster.png")
                .with(new CollidableComponent(true))
                .buildAndAttach();

        //background
        FXGL.entityBuilder()
                .view(new Rectangle(WIDTH, HEIGHT, gradient))
                .zIndex(-100)
                .buildAndAttach();
        //mars
        FXGL.entityBuilder()
                .at(WIDTH/3.4, HEIGHT/5.2)
                .view("mars.png")
                .zIndex(-20)
                .buildAndAttach();
        //FXGL.setLevelFromMap("level1.tmx");

        for (int i = 0; i < NUM_STARS; i++) {
            this.spawnStar();
        }
    }

    private void spawnStar() {
        Entity star = FXGL.entityBuilder()
                .at(Math.random() * WIDTH, Math.random() * HEIGHT)
                .view(new Circle(0.5, Color.WHITE))
                .zIndex(-50)
                .buildAndAttach();

        FXGL.getGameTimer().runAtInterval(() -> {
            if (star.isActive()) {
                star.translateY(STAR_SPEED);
                if (star.getY() > HEIGHT) {
                    star.setPosition(Math.random() * WIDTH, 0);
                }
            }
        }, Duration.millis(0.016));

        FXGL.getGameTimer().runAtInterval(() -> star.translateY(STAR_SPEED), Duration.millis(0.016));
    }

    @Override
    protected void initInput() {
        FXGL.onKey(KeyCode.D, () -> {
            player1.translateX(SPEED); // move right 5 pixels
        });

        FXGL.onKey(KeyCode.A, () -> {
            player1.translateX(-SPEED); // move left 5 pixels
        });

        FXGL.onKey(KeyCode.W, () -> {
            player1.translateY(-SPEED); // move up 5 pixels
        });

        FXGL.onKey(KeyCode.S, () -> {
            player1.translateY(SPEED); // move down 5 pixels
        });
        FXGL.onKey(KeyCode.RIGHT, () -> {
            player2.translateX(SPEED); // move right 5 pixels
        });

        FXGL.onKey(KeyCode.LEFT, () -> {
            player2.translateX(-SPEED); // move left 5 pixels
        });

        FXGL.onKey(KeyCode.UP, () -> {
            player2.translateY(-SPEED); // move up 5 pixels
        });

        FXGL.onKey(KeyCode.DOWN, () -> {
            player2.translateY(SPEED); // move down 5 pixels
        });
    }

    @Override
    protected void initUI() {
        Label power = new Label("Power: ");
        Label gold = new Label("Gold: ");
        Label healthy = new Label("Healthy: ");
        Label numOperationalForces = new Label("Number of Operational Forces: ");
        Label numCastles = new Label("Number of Castles: ");
        power.setFont(Font.font(FONTGAME));
        gold.setFont(Font.font(FONTGAME));
        healthy.setFont(Font.font(FONTGAME));
        numOperationalForces.setFont(Font.font(FONTGAME));
        numCastles.setFont(Font.font(FONTGAME));
        power.setTextFill(Color.YELLOW);
        gold.setTextFill(Color.YELLOW);
        healthy.setTextFill(Color.YELLOW);
        numOperationalForces.setTextFill(Color.YELLOW);
        numCastles.setTextFill(Color.YELLOW);
        FXGL.addUINode(power, 30, 50);
        FXGL.addUINode(gold, 30, 75);
        FXGL.addUINode(healthy, 30, 100);
        FXGL.addUINode(numOperationalForces, 30, 125);
        FXGL.addUINode(numCastles, 30, 150);
    }

    @Override
    protected void initPhysics() {
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER1, EntityType.PLAYER2) {
            @Override
            protected void onCollisionBegin(Entity PLAYER1, Entity PLAYER2) {
                FXGL.getDialogService().showMessageBox("Congratulations! \nyou won! \ngame over.", () ->
                        FXGL.getGameController().gotoMainMenu());
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}