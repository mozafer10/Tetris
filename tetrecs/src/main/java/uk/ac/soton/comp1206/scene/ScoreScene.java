package uk.ac.soton.comp1206.scene;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import uk.ac.soton.comp1206.ui.GameWindow;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;


public class ScoreScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(ScoreScene.class);

    private final Game game;
    private final Communicator communicator;
    private ImageView title;

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public ScoreScene(Game game, GameWindow gameWindow) {
        super(gameWindow);
        this.game = game;

        this.communicator = gameWindow.getCommunicator();
        logger.info("Starting Score scene");
    }

    @Override
    public void initialise() {

    }

    @Override
    public void build() {

        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var scorePane = new StackPane();
        scorePane.setMaxWidth(gameWindow.getWidth());
        scorePane.setMaxHeight(gameWindow.getHeight());
        scorePane.getStyleClass().add("menu-background");
        root.getChildren().add(scorePane);

        var mainPane = new BorderPane();
        scorePane.getChildren().add(mainPane);

        var centerPane = new BorderPane();

        mainPane.setCenter(centerPane);

        //title
        title = new ImageView(new Image(this.getClass().getResource("/images/TetrECS.png").toExternalForm()));
        title.setPreserveRatio(true);
        title.setFitWidth(600);
        centerPane.setCenter(title);

        //vBox for options
        var vbox = new VBox();
        mainPane.setBottom(vbox);
        vbox.setPadding(new Insets(10, 50, 50, 20));
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.BASELINE_CENTER);
        centerPane.setBottom(vbox);

        Text gameOverText = new Text("Game Over!!");
        gameOverText.getStyleClass().add("bigtitle");
        vbox.getChildren().add(gameOverText);

        Text score = new Text("You scored: " + this.game.getScore().toString());
        score.getStyleClass().add("title");
        vbox.getChildren().add(score);

        //adding text options
        var menuOption = menuTextButton("Menu");
        vbox.getChildren().add(menuOption);
        

        //adding events when each text option is clicked
        menuOption.setOnMouseClicked((e) -> {
            gameWindow.startMenu();
        });


    }



    /**
     * Method to create menu buttons.
     */
    public Text menuTextButton(String name) {
        Text textButton = new Text(name);
        textButton.getStyleClass().add("menuItem");
        textButton.setTextAlignment(TextAlignment.CENTER);
        return textButton;
    }



}

