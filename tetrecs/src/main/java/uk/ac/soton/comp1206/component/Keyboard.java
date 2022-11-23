package uk.ac.soton.comp1206.component;


import javafx.scene.Scene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * KeyBoard class that controls all the user driven events.
 * Enables the user to play the game using only keyboard inputs.
 */
public class Keyboard {

    private static final Logger logger = LogManager.getLogger(Keyboard.class);

    /**
     * The x and y coordinates where the user is currently at.
     */
    private int x;
    private int y;

    private final GameBoard gameBoard;


    /**
     * Returns the x coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Instantiates a new Keyboard, sets the initial coordinates to {0,0}.
     *
     * @param gameBoard the game board
     */
    public Keyboard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        x = 0;
        y = 0;
    }


    /**
     * Moves the position "up" by decreasing y, if it is within the bounds.
     */
    public void movePositionUp() {
        if (y >= 1) {
            y -= 1;
        }
    }

    /**
     * Moves the position "down" by increasing y, if it is within the bounds.
     */
    public void movePositionDown() {
        if (y < (gameBoard.getGrid().getRows() - 1)) {
            y += 1;
        }
    }

    /**
     * Moves the position "left" by decreasing x, if it is within the bounds.
     */
    public void movePositionLeft() {
        if (x > 0) {
            x -= 1;
        }
    }

    /**
     * Moves the position "right" by increasing x, if it is within the bounds.
     */
    public void movePositionRight() {
        if (x < (gameBoard.getGrid().getCols() - 1)) {
            x += 1;
        }
    }

    /**
     * Move takes in the input of the user in a given scene and controls the hover when using the keyboard to navigate.
     *
     * @param scene
     * @param game
     * @param gameWindow
     */
    public void move(Scene scene, Game game, GameWindow gameWindow) {

        scene.setOnKeyPressed((e) -> {

            //remove hover on current block
            removeHover(getCurrentBlock());

            switch (e.getCode())
            {
                //ESCAPE to go back to menu
                case ESCAPE:
                    game.gameTimer.cancel();
                    gameWindow.startMenu();
                    break;
                //UP or W to move position up
                case W:
                case UP:
                    movePositionUp();
                    break;
                //DOWN or S to move position down
                case DOWN:
                case S:
                    movePositionDown();
                    break;
                //LEFT or A to move position left
                case LEFT:
                case A:
                    movePositionLeft();
                    break;
                //RIGHT or D to move position up
                case RIGHT:
                case D:
                    movePositionRight();
                    break;
                //ENTER or X to play current piece at current x,y
                case ENTER:
                case X:
                    game.blockClicked((gameBoard.getBlock(x, y)));
                    break;
                //SPACE or R to swap current piece
                case SPACE:
                case R:
                    game.swapCurrentPiece();
                    break;
                //Q or Z or [ to swap to rotate current piece right
                case Q:
                case Z:
                case OPEN_BRACKET:
                    rotatePieceRight(game);
                    break;
                //E or C or ] to swap to rotate current piece left
                case E:
                case C:
                case CLOSE_BRACKET:
                    rotatePieceLeft(game);
                    break;
                //key with no use
                default:
                    logger.info("This key has no use!");

            }
            //add hover to current block
            hoverCurrentBlock(getCurrentBlock());
        });

        //remove the hover on the block which the mouse is hovered on to avoid conflict
        gameBoard.setOnMouseMoved((e) -> {
            gameBoard.getBlock(x, y).setHover(false);
            gameBoard.getBlock(x, y).paint();
        });
    }

    /**
     * Rotates the current piece once.
     *
     * @param game Game
     */
    private void rotatePieceLeft(Game game) {
        game.rotateCurrentPiece();
    }

    /**
     * Rotates the current piece 3 times (rotate right).
     *
     * @param game Game
     */
    private void rotatePieceRight(Game game) {
        game.rotateCurrentPiece();
        game.rotateCurrentPiece();
        game.rotateCurrentPiece();
    }


    /**
     * Sets hover on the current block to true and repaints it.
     *
     * @param currentBlock GameBlock
     */
    private void hoverCurrentBlock(GameBlock currentBlock) {
        currentBlock.setHover(true);
        currentBlock.paint();
        logger.info("Just added hover to the block: {{}{}}", getX(), getY());
    }

    /**
     * Returns the current gameBlock
     *
     * @return GameBlock
     */
    private GameBlock getCurrentBlock() {
        GameBlock currentBlock = gameBoard.getBlock(getX(), getY());
        return currentBlock;
    }
    /**
     * Sets hover on the current block to false and repaints it.
     *
     * @param currentBlock GameBlock
     */
    private void removeHover(GameBlock currentBlock) {
        currentBlock.setHover(false);
        currentBlock.paint();
    }
}