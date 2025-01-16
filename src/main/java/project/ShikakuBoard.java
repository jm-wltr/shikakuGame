package project;

/*
 * Shikaku Game
 * Adapted from my final project for CIS 1200 at the University of Pennsylvania
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This class instantiates a Shikaku object, which is the model for the game.
 * As the user clicks the game board, the model is updated. Whenever the model
 * is updated, the game board repaints itself and updates its status JLabel to
 * reflect the current state of the model.
 *
 * This implementation adheres to a Model-View-Controller (MVC) design
 * framework,
 * which is particularly effective for turn-based games. The MVC pattern
 * separates
 * the game logic (model) from the user interface (view), with a controller
 * to manage interactions between the two.
 *
 * In this framework:
 * - The ShikakuBoard class represents the 'View' and 'Controller', handling the
 * game's visual
 * representation and user interactions.
 * - The Shikaku class serves as the 'Model', encapsulating the core game logic
 * and state.
 * - RunShikaku initializes the GUI, setting up the game board and other
 * interface elements
 * like buttons for game controls and instructions.
 */

@SuppressWarnings("serial")
public class ShikakuBoard extends JPanel {

    private Shikaku shikaku; // model for the game
    private JLabel timerLabel; // current status text
    private boolean startMode; // true if startMode, false if endMode

    private int startCol, startRow;

    /**
     * Initializes the game board.
     */
    public ShikakuBoard(JLabel timerInit) {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        timerLabel = timerInit;

        shikaku = new Shikaku(); // initializes model for the game
        Timer guiUpdateTimer = new Timer(150, e -> updateTimer());
        guiUpdateTimer.start();

        startMode = true;

        /*
         * Listens for mouseclicks. Updates the model, then updates the game
         * board based off of the updated model.
         */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int cellSize = Math.min(getWidth(), getHeight()) / shikaku.getSize();

                // Calculate the clicked cell coordinates
                int colClicked = e.getX() / cellSize;
                int rowClicked = e.getY() / cellSize;

                if (startMode) {
                    if (!shikaku.isGameOver()) {
                        if (shikaku.getCellValue(rowClicked, colClicked) != 0) {
                            // Delete the rectangle
                            shikaku.resetTiles(shikaku.getCellValue(rowClicked, colClicked));
                            repaint();
                        } else {
                            // Start drawing a new rectangle
                            startCol = colClicked;
                            startRow = rowClicked;
                            startMode = false;
                        }
                    }
                } else {
                    // In end mode, finalize the rectangle
                    boolean valid = shikaku.playTurn(startCol, startRow, colClicked, rowClicked);

                    if (!valid) {
                        // Flash red and disappear (visual feedback)
                        flashInvalidRectangle(startCol, startRow, colClicked, rowClicked);
                    } else {
                        repaint();
                    }
                    startMode = true;
                }

            }
        });

    }

    /**
     * Sets the game to a new 5x5 game.
     */
    public void newGame(int boardSize) {
        shikaku.reset(boardSize);
        startMode = true;
        repaint();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    /**
     * Undoes the last action performed in the game.
     * Triggers the undo functionality in the Shikaku model and repaints the game
     * board.
     */
    public void undo() {
        shikaku.undo();
        repaint();
    }

    /**
     * Redoes the last undone action in the game.
     * Triggers the redo functionality in the Shikaku model and repaints the game
     * board.
     */
    public void redo() {
        shikaku.redo();
        repaint();
    }

    /**
     * Updates the JLabel to reflect the current state of the game.
     */
    private void updateTimer() {
        String formattedTime = shikaku.getFormattedTime();
        timerLabel.setText(formattedTime);
        if (showInvalidRectangle && System.currentTimeMillis() - invalidRectStartTime >= 150) {
            showInvalidRectangle = !showInvalidRectangle;
            repaint();
        }
    }

    private boolean showInvalidRectangle;
    private int invalidRectStartCol, invalidRectStartRow;
    private int invalidRectEndCol, invalidRectEndRow;
    private long invalidRectStartTime;

    /**
     * Temporarily highlights an invalid rectangle on the game board. This method is
     * used
     * to provide visual feedback when a player attempts to create an invalid
     * rectangle.
     * It sets up the necessary parameters to draw the rectangle in a distinct color
     * (typically red) to indicate the mistake.
     *
     * The method stores the start and end coordinates of the rectangle and marks it
     * to
     * be shown on the game board. The actual drawing of the rectangle is handled in
     * the
     * `paintComponent` method of the board. This method also records the start time
     * for
     * the flashing effect, allowing the view to manage the duration of the flash.
     *
     * @param startCol The starting column of the invalid rectangle.
     * @param startRow The starting row of the invalid rectangle.
     * @param endCol   The ending column of the invalid rectangle.
     * @param endRow   The ending row of the invalid rectangle.
     */
    private void flashInvalidRectangle(int startCol, int startRow, int endCol, int endRow) {
        showInvalidRectangle = true;
        invalidRectStartCol = startCol;
        invalidRectStartRow = startRow;
        invalidRectEndCol = endCol;
        invalidRectEndRow = endRow;
        invalidRectStartTime = System.currentTimeMillis();

        repaint();
    }

    /**
     * Generates a pastel color based on the given rectangle ID. This method uses
     * the rectangle ID
     * to create a variation in the hue, which is then used to generate a pastel
     * color.
     *
     * The method uses the Hue, Saturation, and Brightness (HSB) color model to
     * create a color.
     * The hue is varied using the rectangle ID to ensure a range of different
     * colors for different IDs.
     * The saturation and brightness values are set to create the soft tones typical
     * of pastel colors.
     *
     * @param rectID The rectangle identifier used to generate the color.
     * @return A pastel color generated based on the rectangle ID.
     */
    private Color getPastelColor(int rectID) {
        // Use rectID to create variation in hue
        float hue = (rectID * 137.5f) % 360; // Using a prime number to create variation
        hue /= 360f; // Normalize to 0-1 range
        float saturation = 0.6f; // Slightly higher saturation for pastel
        float brightness = 0.9f; // High brightness for pastel
        return Color.getHSBColor(hue, saturation, brightness);
    }

    /**
     * Draws the game board.
     *
     * There are many ways to draw a game board. This approach
     * will not be sufficient for most games, because it is not
     * modular. All of the logic for drawing the game board is
     * in this method, and it does not take advantage of helper
     * methods. Consider breaking up your paintComponent logic
     * into multiple methods or classes, like Mushroom of Doom.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Determine the size of each cell and the board
        int windowSize = Math.min(getWidth(), getHeight());
        int cellSize = windowSize / shikaku.getSize();
        int boardSize = cellSize * shikaku.getSize();

        // Color the entire background light gray
        g.setColor(new Color(230, 230, 230));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw a white rectangle for the game board area
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, boardSize, boardSize);

        // Set the color back to default (black) for drawing the grid
        g.setColor(Color.BLACK);

        // Draw colored tiles
        for (int i = 0; i < shikaku.getSize(); i++) {
            for (int j = 0; j < shikaku.getSize(); j++) {
                int rectId = shikaku.getCellValue(i, j);
                if (rectId > 0) {
                    g.setColor(getPastelColor(rectId));
                    g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                }
            }
        }

        // Draw grid lines on top of the colored tiles
        g.setColor(Color.BLACK);
        for (int i = 0; i <= shikaku.getSize(); i++) {
            int linePos = i * cellSize;
            g.drawLine(linePos, 0, linePos, shikaku.getSize() * cellSize);
            g.drawLine(0, linePos, shikaku.getSize() * cellSize, linePos);
        }

        // Draw the initial numbers on the board
        int[][] initialNumbers = shikaku.getInitialNumbers();
        g.setColor(Color.BLACK); // Set color for drawing text
        Font numberFont = new Font(
                "Default", Font.BOLD,
                shikaku.getSize() == 5 ? cellSize / 3 : cellSize / 2
        );
        g.setFont(numberFont);
        FontMetrics metrics = g.getFontMetrics(numberFont);

        for (int i = 0; i < shikaku.getSize(); i++) {
            for (int j = 0; j < shikaku.getSize(); j++) {
                int number = initialNumbers[i][j];
                if (number > 0) {
                    String numberStr = String.valueOf(number);
                    int x = j * cellSize + (cellSize - metrics.stringWidth(numberStr)) / 2;
                    int y = i * cellSize + ((cellSize - metrics.getHeight()) / 2)
                            + metrics.getAscent();
                    g.drawString(numberStr, x, y);
                }
            }
        }

        if (showInvalidRectangle) {
            g.setColor(Color.RED);

            // Determine the top-left corner of the rectangle
            int x = Math.min(invalidRectStartCol, invalidRectEndCol) * cellSize;
            int y = Math.min(invalidRectStartRow, invalidRectEndRow) * cellSize;

            // Calculate the width and height of the rectangle
            int width = (Math.abs(invalidRectEndCol - invalidRectStartCol) + 1) * cellSize;
            int height = (Math.abs(invalidRectEndRow - invalidRectStartRow) + 1) * cellSize;

            g.fillRect(x, y, width, height);
            g.setColor(Color.BLACK); // Reset color for other drawing
        }

    }

    /**
     * Returns the size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 600);
    }
}
