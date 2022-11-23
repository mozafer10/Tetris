package uk.ac.soton.comp1206.scene;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.*;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.*;
import uk.ac.soton.comp1206.game.*;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(ChallengeScene.class);

    protected Game game;

    private GameBoard mainGameBoard;

    //keyboard object to control all the user inputs
    private Keyboard keyboard;

    //small gameBoards to hold the current and upcoming pieces
    private PieceBoard currentPieceBoard;
    private PieceBoard upcomingPieceBoard;

    private CheckBox mute;

    private Slider volumeSlider;

    private final IntegerProperty highScore = new SimpleIntegerProperty();

    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        StackPane challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        //adding borderPanes to place corresponding areas in the mainPain to allow for better positioning
        BorderPane mainPane;
        mainPane = new BorderPane();
        BorderPane leftPane;
        leftPane = new BorderPane();
        BorderPane topPane;
        topPane = new BorderPane();
        BorderPane rightPane;
        rightPane = new BorderPane();
        BorderPane bottomPane;
        bottomPane = new BorderPane();
        BorderPane centrePane;
        centrePane = new BorderPane();

        //placing the panes in the mainPain
        mainPane.setLeft(leftPane);
        mainPane.setTop(topPane);
        mainPane.setRight(rightPane);
        mainPane.setBottom(bottomPane);
        mainPane.setCenter(centrePane);

        //creating vBox to hold the level and pieceBoards
        VBox rightVbox = new VBox();
        rightPane.setCenter(rightVbox);
        rightVbox.setAlignment(Pos.CENTER);
        rightVbox.setPadding(new Insets(0, 30, 0, 0));

        //creating vBox to hold the settings
        VBox leftVbox = new VBox();
        leftPane.setCenter(leftVbox);
        leftVbox.setAlignment(Pos.CENTER);
        leftVbox.setPadding(new Insets(0, 30, 0, 0));

        challengePane.getChildren().add(mainPane);

        //Add a title
        Text challengeModeTitle = new Text("Challenge Mode");
        challengeModeTitle.getStyleClass().add("title");
        topPane.setCenter(challengeModeTitle);

        //Add a VBox to fill with score and level
        VBox vBoxScores_Slider_Check = new VBox();
        VBox vBoxLives = new VBox();
        Text scoreLabel = new Text();
        Text scoreTitle = new Text("Score");
        Text levelLabel = new Text();
        Text levelTitle = new Text("Level");
        Text livesLabel = new Text();
        Text livesTitle = new Text("Lives");

        //adding style and binding the lives label to the correct game property
        livesLabel.getStyleClass().add("lives");
        livesTitle.getStyleClass().add("heading");
        livesLabel.textProperty().bind(game.livesProperty().asString());
        for (Iterator<Text> iterator = Arrays.asList(livesTitle, livesLabel).iterator(); iterator.hasNext(); ) {
            Text text = iterator.next();
            vBoxLives.getChildren().add(text);
        }

        //adding style and binding the score label to the correct game property
        scoreLabel.getStyleClass().add("score");
        scoreTitle.getStyleClass().add("heading");
        scoreLabel.textProperty().bind(game.scoreProperty().asString());
        vBoxScores_Slider_Check.getChildren().add(scoreTitle);
        vBoxScores_Slider_Check.getChildren().add(scoreLabel);


        //adding style and binding the level label to the correct game property
        levelLabel.getStyleClass().add("level");
        levelTitle.getStyleClass().add("heading");
        levelLabel.textProperty().bind(game.levelProperty().asString());

        //add scoreboards to the challengePane
        topPane.setLeft(vBoxScores_Slider_Check);
        vBoxScores_Slider_Check.setAlignment(Pos.TOP_CENTER);
        vBoxScores_Slider_Check.setPadding(new Insets(10, 10, 10, 10));

        topPane.setRight(vBoxLives);
        vBoxLives.setAlignment(Pos.TOP_CENTER);
        vBoxLives.setPadding(new Insets(10, 10, 10, 10));

        //Add level to vbox
        rightVbox.getChildren().add(levelTitle);
        rightVbox.getChildren().add(levelLabel);

        //add incoming text
        var upcomingPiece = new Text("Incoming");
        upcomingPiece.getStyleClass().add("heading");
        rightVbox.getChildren().add(upcomingPiece);

        BorderPane paneInsideRightVBox1 = new BorderPane();
        rightVbox.getChildren().add(paneInsideRightVBox1);

        BorderPane paneInsideRightVBox2 = new BorderPane();
        rightVbox.getChildren().add(paneInsideRightVBox2);

        //add timer
        var vBoxInsideBottomPane = new VBox();
        var timerBar = new Rectangle(0, gameWindow.getWidth() + 2, gameWindow.getWidth() / 1.2,
                gameWindow.getHeight() / 28);
        //Border
        timerBar.setFill(Color.GREEN);

        //initialising the current piece board with the current piece being displayed
        currentPieceBoard = new PieceBoard(game.getCurrentPiece(), gameWindow.getWidth() / 6, gameWindow.getWidth() / 6, true);
        paneInsideRightVBox1.setCenter(currentPieceBoard);
        currentPieceBoard.setPadding(new Insets(10, 0, 0, 0));

        //initialising the upcoming piece board with the following piece being displayed
        upcomingPieceBoard = new PieceBoard(game.getFollowingPiece(), gameWindow.getWidth() / 8, gameWindow.getWidth() / 8, false);
        paneInsideRightVBox2.setCenter(upcomingPieceBoard);
        upcomingPieceBoard.setPadding(new Insets(10, 0, 0, 0));

        //add the timer to the bottom pane
        vBoxInsideBottomPane.getChildren().add(timerBar);
        vBoxInsideBottomPane.setAlignment(Pos.BOTTOM_LEFT);
        vBoxInsideBottomPane.setPadding(new Insets(0, 0, 3, 0));
        bottomPane.setCenter(vBoxInsideBottomPane);

        //add game board
        mainGameBoard = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2, gameWindow.getWidth() / 2);
        centrePane.setCenter(mainGameBoard);

        //initialising the keyboard with the gameBoard as a parameter
        keyboard = new Keyboard(mainGameBoard);

        //Handle block on gameBoard grid being clicked
        mainGameBoard.setOnBlockClick(this::blockClicked);

        //calling the correct methods with the new pieces
        game.setNextPieceListener((gamePiece, followingPiece) -> {
            receiveGamePiece(gamePiece);
            receiveNextGamePiece(followingPiece);
        });

        //calling the correct method with the number of lives
        game.setGameLivesListener(lives -> receiveNumberOfLives(lives));

        //calling the correct method to with the blocks that need to be cleared
        game.setLineClearedListener(setOfCoordinates -> receiveCoordinatesToClear(setOfCoordinates));

        //calling the correct method to create a new timer with the correct time
        game.setGameLoopListener((timer) -> {
            Platform.runLater(() ->
                    timer(timerBar, game.getTimerDelay()));
        });

        //calling the swap method when the upcomingPieceBoard is left clicked
        upcomingPieceBoard.setOnBlockClick((e) -> {
            game.swapCurrentPiece();
        });

        //calling the rotate method when the mainGameBoard is right clicked
        mainGameBoard.setOnRightClick((e) -> {
            game.rotateCurrentPiece();
        });

        //calling the rotate method when the currentPieceBoard is left clicked
        currentPieceBoard.setOnBlockClick((e) -> {
            game.rotateCurrentPiece();
        });
    }

    /**
     * Handle when a block is clicked
     *
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();

        //set up keyboard with correct parameters
        keyboard.move(scene, game, gameWindow);



        volumeSlider.setValue(Multimedia.musicPlayer.getVolume() * 100);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                Multimedia.musicPlayer.setVolume(volumeSlider.getValue() / 100);
            }
        });
    }

    /**
     * Receive a gamePiece and display it on the current board.
     */
    public void receiveGamePiece(GamePiece piece) {
        logger.info("Receiving the next piece {}", piece.toString());
        currentPieceBoard.setPieceToDisplay(piece);
    }

    /**
     * This method recieves coordinates to be cleared and faded out.
     */
    public void receiveCoordinatesToClear(Set<int[]> setOfCoordinates) {
        logger.info("Receiving the set of coordinates to clear");
        mainGameBoard.fadeOut(setOfCoordinates);
    }

    /**
     * This method displays gamePiece after it has been received onto the board.
     */
    public void receiveNextGamePiece(GamePiece piece) {
        logger.info("Receiving the Following piece {}", piece.toString());
        upcomingPieceBoard.setPieceToDisplay(piece);
    }

    /**
     * This method recieves the number of lives so the game ends when it becomes zero.
     */
    public void receiveNumberOfLives(int lives) {
        switch (lives) {
            case -1 -> Platform.runLater(() -> {
                gameWindow.startScores(this.game);
            });
        }
    }

    /**
     * Animates a timer with colours from green to red according to the time value passed to it.
     * The closer the timer is too finishing the the more "urgent" the colour will become
     *
     * @param timer       a rectangle which will be animated
     * @param initialTime the initial time for the timer
     */
    public void timer(Rectangle timer, int initialTime) {

        double calender = Calendar.getInstance().getTimeInMillis();
        double ratio = 1;

        //setting the timer width
        timer.setWidth(ratio * gameWindow.getWidth());

        //AnimationTimer class
        AnimationTimer animationTimer = new AnimationTimer() {
            double timeLeft = initialTime;
            int red;
            int green;

            @Override
            public void handle(long l) {
                //if the time has not run out yet
                if (!(timeLeft > 0)) {
                    stop();
                } else {
                    Calendar currentCalender = Calendar.getInstance();
                    timeLeft = initialTime - (currentCalender.getTimeInMillis() - calender);
                    double ratio = timeLeft / initialTime;
                    timer.setWidth(ratio * gameWindow.getWidth());

                    //change the value of the RGB values linearly according to the ratio value
                    if (!(ratio > 0.9)) if (ratio <= 0.9 && ratio > 0.5) {
                        green = (int) Math.floor(-317.5 * ratio + 413.75);
                        red = (int) Math.floor(-637.5 * ratio + 573.75);
                    } else {
                        if (ratio > 0.1 && ratio < 0.5) {
                            green = (int) Math.floor(637.5 * ratio - 63.75);
                        } else if (ratio < 0.1) green = 0;
                    }
                    else {
                        green = 128;
                    }
                    //set the colour of the timer with the updates RGB values
                    timer.setFill(Color.rgb(red, green, 0));
                }
            }
        };
        animationTimer.start();
    }

    private void startScore() {
        Platform.runLater(() -> {
            game.gameTimer.cancel();
        });
    }

    /**
     * Sets score.
     *
     * @param observableValue the observable value
     * @param old             the old
     * @param newNum          the new num
     */
    protected void setScore(ObservableValue<? extends Number> observableValue, Number old, Number newNum) {
        logger.info("Score is now {}", newNum);
        if (newNum.intValue() > this.highScore.get()) this.highScore.set(newNum.intValue());
    }
}