package shikaku;

/*
 * Shikaku Game
 * Adapted from my final project for CIS 1200 at the University of Pennsylvania
 */

import java.util.LinkedList;
import java.util.Random;
import javax.swing.Timer;

/**
 * This class is a model for Shikaku.
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
public class Shikaku {

    private int[][] board;
    private boolean gameOver;
    private int rectID;
    private int size;
    private int[][] initialNumbers;

    private Timer timer;
    private long startTime;
    private long elapsedTime;

    private Random rand;

    private LinkedList<int[][]> undoList;
    private LinkedList<int[][]> redoList;

    /**
     * Constructor for the Shikaku game.
     * Initializes the game with a default board size.
     * This setup includes initializing the game board, setting up the timer,
     * and preparing the undo and redo lists for gameplay.
     * The default board size is set to 5x5, but it can be changed
     * using the reset method with a different size.
     */
    public Shikaku() {
        reset(5);
    }

    /**
     * Resets the game state to a new board of the specified size.
     * This method stops the existing timer, initializes a new game board
     * with the given size, and sets up the game state for a new game.
     * It also initializes the puzzle generation process and restarts the timer.
     * Additionally, it clears and prepares the undo and redo lists for the new
     * game.
     *
     * @param boardSize The size of the board (number of cells in one dimension).
     */
    public void reset(int boardSize) {
        if (timer != null) {
            timer.stop();
        }

        this.board = new int[boardSize][boardSize];
        this.gameOver = false;
        this.rectID = 1;
        this.size = boardSize;
        initializeBoardForGeneration();
        generateValidBoard(0, boardSize, boardSize, 0);

        this.startTime = System.currentTimeMillis();
        this.timer = new Timer(1000, e -> updateTimer());
        timer.start();

        this.undoList = new LinkedList<>();
        this.redoList = new LinkedList<>();

    }

    /**
     * Attempts to play a turn by placing a rectangle on the board. The rectangle
     * is defined by two diagonal corners at coordinates (c1, r1) and (c2, r2).
     * Returns true if the move is successful. A move is considered successful if
     * it meets the following criteria:
     * - Both sets of coordinates are within the bounds of the board.
     * - The area defined by the rectangle does not overlap with any existing
     * rectangles.
     * - The game has not yet ended.
     * If the turn is successful, the area of the new rectangle is marked with a
     * unique
     * identifier on the board. Unsuccessful turns, due to invalid coordinates,
     * overlap,
     * or the game being over, result in no changes to the board. The method also
     * handles
     * undo and redo list updates for the turn.
     *
     * @param c1 Column index of the first corner.
     * @param r1 Row index of the first corner.
     * @param c2 Column index of the opposite corner.
     * @param r2 Row index of the opposite corner.
     * @return true if the turn is successful, false otherwise.
     */
    public boolean playTurn(int c1, int r1, int c2, int r2) {
        // Check if the game is already over
        if (gameOver) {
            return false;
        }

        // Check if coordinates are within bounds
        if (c1 < 0 || c1 >= size || r1 < 0 || r1 >= size ||
                c2 < 0 || c2 >= size || r2 < 0 || r2 >= size) {
            return false;
        }

        // Normalize coordinates
        int left = Math.min(c1, c2);
        int right = Math.max(c1, c2);
        int top = Math.min(r1, r2);
        int bottom = Math.max(r1, r2);

        // Check if the area is free
        for (int i = top; i <= bottom; i++) {
            for (int j = left; j <= right; j++) {
                if (board[i][j] != 0) {
                    return false;
                }
            }
        }

        redoList.clear();
        undoList.add(copyOf(board));
        // Fill the area
        for (int i = top; i <= bottom; i++) {
            for (int j = left; j <= right; j++) {
                board[i][j] = rectID;
            }
        }

        rectID++;
        checkGameOver();
        return true;
    }

    /**
     * Resets all tiles with a specific rectangle ID to zero.
     * This is used to remove a rectangle from the board.
     *
     * @param rectId The rectangle ID to reset.
     */
    public void resetTiles(int rectId) {
        redoList.clear();
        undoList.add(copyOf(board));
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == rectId) {
                    board[i][j] = 0;
                }
            }
        }
    }

    /**
     * Undoes the last move made in the game, if possible.
     * This method reverts the game board to its state before the most recent move.
     * It can be called multiple times to sequentially undo previous moves.
     * The method does nothing if there are no moves to undo or if the game is over.
     * It also transfers the current game state to the redo list before undoing the
     * move,
     * allowing the move to be redone if needed.
     */
    public void undo() {
        if (!undoList.isEmpty() && !gameOver) {
            redoList.add(copyOf(board));
            board = undoList.removeLast();
        }
    }

    /**
     * Redoes the most recently undone move in the game, if possible.
     * This method restores the game board to its state after an undone move.
     * It can be called multiple times to sequentially redo previously undone moves.
     * The method does nothing if there are no moves to redo or if the game is over.
     * It also transfers the restored game state to the undo list, allowing further
     * undos if needed.
     */
    public void redo() {
        if (!redoList.isEmpty() && !gameOver) {
            undoList.add(copyOf(board));
            board = redoList.removeLast();
        }
    }

    /**
     * Returns the elapsed game time in a formatted string.
     * The format of the returned string is HH:mm:ss, where HH is hours,
     * mm is minutes, and ss is seconds.
     *
     * @return A string representing the elapsed time since the start of the game.
     */
    public String getFormattedTime() {
        int seconds = (int) (elapsedTime / 1000) % 60;
        int minutes = (int) ((elapsedTime / (1000 * 60)) % 60);
        int hours = (int) ((elapsedTime / (1000 * 60 * 60)) % 24);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Provides a copy of the current game board.
     * This method returns a 2D array where each cell represents a part of a
     * rectangle
     * or is empty. Modifying the returned array does not affect the internal game
     * state.
     *
     * @return A 2D array representing the current state of the game board.
     */
    public int[][] getBoard() {
        return copyOf(board);
    }

    /**
     * Provides a copy of the initial numbers array for the Shikaku puzzle.
     * This array represents the starting state of the puzzle, where each cell
     * contains a number indicating the area of the rectangle to be formed, or 0 if
     * empty.
     * Modifying the returned array does not affect the internal game state.
     *
     * @return A 2D array representing the initial numbers of the puzzle.
     */
    public int[][] getInitialNumbers() {
        return copyOf(initialNumbers);
    }

    /**
     * Gets size of the board.
     *
     * @return Size of the board
     */
    public int getSize() {
        return size;
    }

    /**
     * Checks if the game has ended.
     *
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Retrieves the ID to be assigned to the next new rectangle.
     *
     * @return The current rectangle ID.
     */
    public int getCurrentRectID() {
        return rectID;
    }

    /**
     * Gets the value at a specific cell in the game board.
     * If the row or column indices are out of bounds, returns -1 or throws an
     * exception.
     *
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     * @return The value at the specified cell, or -1 for invalid indices.
     */
    public int getCellValue(int row, int col) {
        if (row >= 0 && row < size && col >= 0 && col < size) {
            return board[row][col];
        }
        return -1; // Or throw an exception for invalid indices
    }

    // HELPERS TO PLAY MOVES

    /**
     * Checks if the game is over by verifying two conditions:
     * 1. Every cell on the board is part of a rectangle (i.e., no cell is left
     * empty).
     * 2. Each rectangle's area matches the size specified in the initialNumbers
     * array.
     * A rectangle's area is calculated based on the number of cells it occupies on
     * the board.
     * The game is considered over if all rectangles are correctly placed according
     * to these
     * conditions. If any cell is empty or if any rectangle does not match the
     * required size,
     * the game is not yet over. It also stops the time if the game is over.
     *
     * @return true if the game is over with a correct solution, false otherwise.
     */
    public boolean checkGameOver() {
        if (isBoardFull(board)) {
            // Check if each rectangle matches the size specified in initialNumbers
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    int targetArea = initialNumbers[r][c];
                    if (targetArea > 0) {
                        int actualArea = calculateArea(r, c);
                        if (actualArea != targetArea) {
                            return false; // Rectangle size does not match
                        }
                    }
                }
            }
            gameOver = true;
            if (timer != null) {
                timer.stop();
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if all cells in the provided board are part of a rectangle.
     * In the context of Shikaku, a cell is part of a rectangle if it is not empty
     * (i.e., not zero).
     * This method also handles irregular (non-square or jagged) board sizes.
     * <p>
     * The method will return false in the following cases:
     * - If the board is null.
     * - If the board is empty (zero rows).
     * - If any row in the board is null.
     * - If the board is jagged (rows have different lengths).
     * - If any cell in the board is empty (zero value).
     *
     * @param myBoard The 2D array representing the Shikaku game board.
     * @return true if all cells are part of a rectangle, false otherwise.
     */
    private boolean isBoardFull(int[][] myBoard) {
        if (myBoard == null || myBoard.length == 0) {
            return false; // The board is null or empty, cannot be full
        }

        for (int[] row : myBoard) {
            if (row == null || row.length != myBoard[0].length) {
                return false; // Found a null or irregular row
            }

            for (int cell : row) {
                if (cell == 0) {
                    return false; // Found an empty cell, the board is not full
                }
            }
        }

        return true; // All cells are part of a rectangle
    }

    /**
     * Calculates the area of the rectangle that includes the cell at (r, c).
     * Assumes that each rectangle is correctly formed with unique identifiers.
     *
     * @param r The row index.
     * @param c The column index.
     * @return The area of the rectangle.
     */
    private int calculateArea(int r, int c) {
        int rectId = board[r][c];
        int area = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == rectId) {
                    area++;
                }
            }
        }
        return area;
    }

    // HELPERS TO GENERATE BOARDS

    /**
     * Initializes the board for puzzle generation. This method sets up the random
     * number
     * generator and resets the initial numbers array to the current size of the
     * board.
     * The initialNumbers array is used to store the numbers that each rectangle
     * must match
     * in the puzzle.
     */
    public void initializeBoardForGeneration() {
        this.rand = new Random(System.nanoTime());
        this.initialNumbers = new int[size][size]; // Reset initial numbers
    }

    /**
     * Generates a valid Shikaku puzzle board. This method repeatedly attempts to
     * generate
     * a puzzle board until it creates one where the largest number in the
     * initialNumbers
     * array does not exceed a specified limit (size + 10). This limit ensures a
     * balanced
     * level of difficulty for the puzzle.
     *
     * @param left   Left boundary of the board.
     * @param bottom Bottom boundary of the board.
     * @param right  Right boundary of the board.
     * @param top    Top boundary of the board.
     */
    private void generateValidBoard(int left, int bottom, int right, int top) {
        while (true) {
            initializeBoardForGeneration();
            generateBoard(left, bottom, right, top);
            boolean condition = true;
            for (int[] row : initialNumbers) {
                for (int value : row) {
                    if (value >= size + 10) {
                        condition = false;
                    }
                }
            }
            if (condition) {
                break;
            }

        }
    }

    /**
     * Recursively generates a Shikaku puzzle board. This method divides the board
     * into
     * sections, each containing a unique number that represents the area of the
     * rectangle
     * that needs to be formed in that section.
     *
     * @param left   Left boundary of the current section.
     * @param bottom Bottom boundary of the current section.
     * @param right  Right boundary of the current section.
     * @param top    Top boundary of the current section.
     */
    private void generateBoard(int left, int bottom, int right, int top) {
        int xsplit = randCoord(left, right);
        int ysplit = randCoord(bottom, top);

        if (rand.nextBoolean()) {
            if (validSplit(top, bottom, ysplit, right - left)) {
                generateBoard(left, bottom, right, ysplit);
                generateBoard(left, ysplit, right, top);
            } else {
                int xcoord = randCoord(left, right);
                int ycoord = randCoord(bottom, top);
                initialNumbers[ycoord][xcoord] = (bottom - top) * (right - left);
            }
        } else {
            if (validSplit(left, right, xsplit, bottom - top)) {
                generateBoard(left, bottom, xsplit, top);
                generateBoard(xsplit, bottom, right, top);
            } else {
                int xcoord = randCoord(left, right);
                int ycoord = randCoord(bottom, top);
                initialNumbers[ycoord][xcoord] = (bottom - top) * (right - left);
            }
        }
    }

    /**
     * Checks if a proposed split of the board is valid based on the specified
     * dimensions.
     * A split is considered valid if it results in two areas, each having an area
     * of at least 2.
     *
     * @param min    Minimum boundary of the split.
     * @param max    Maximum boundary of the split.
     * @param split  Proposed split position.
     * @param height Height of the area being split.
     * @return true if the split is valid, false otherwise.
     */
    private boolean validSplit(int min, int max, int split, int height) {
        int area1 = (split - min) * height;
        int area2 = (max - split) * height;
        return area1 >= 2 && area2 >= 2;
    }

    /**
     * Generates a random coordinate within a specified range.
     *
     * @param from Lower bound of the range.
     * @param to   Upper bound of the range.
     * @return A random coordinate within the specified range.
     */
    private int randCoord(int from, int to) {
        return rand.nextInt(Math.abs(to - from)) + Math.min(to, from);
    }

    // OTHER HELPERS

    /**
     * Updates the elapsed time since the start of the game. This method calculates
     * the time difference between the current system time and the start time of the
     * game.
     * The elapsed time is used to update the game's timer display.
     */
    private void updateTimer() {
        // Calculate elapsed time in milliseconds
        elapsedTime = System.currentTimeMillis() - startTime;

        // Optionally, notify the view (e.g., a GUI component) to update the timer
        // display
        // This can be done via an observer pattern, a direct method call, etc.
    }

    /**
     * Creates a deep copy of a square matrix (2D array).
     * This method is designed to work with square arrays where the number of rows
     * and columns are equal.
     * It returns a new 2D array with the same values as the original array,
     * ensuring
     * that modifications to the copied array do not affect the original array.
     * If the input array is not square or is empty, the method returns null.
     *
     * @param originalArray The square matrix (2D array) to be copied.
     * @return A new deep copy of the original square matrix, or null if the
     *         original array is not square or is empty.
     */
    private int[][] copyOf(int[][] originalArray) {
        // Check if the array is square
        if (originalArray.length == 0 || originalArray.length != originalArray[0].length) {
            return null; // Return null for non-square arrays or empty arrays
        }

        int[][] copy = new int[originalArray.length][originalArray.length];
        for (int i = 0; i < originalArray.length; i++) {
            System.arraycopy(originalArray[i], 0, copy[i], 0, originalArray[i].length);
        }
        return copy;
    }

    // PUBLIC METHODS FOR DEBUGGING

    /**
     * printGameState prints the current game state
     * (board) for debugging.
     */
    public void printGameState() {
        System.out.println("\nCurrent Game State:\n");

        // Assuming 'board' is a square grid
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                // Print the cell value, formatted for alignment
                System.out.printf("%4d", board[i][j]);
            }
            System.out.println(); // New line at the end of each row
            if (i < board.length - 1) {
                // Print a separator line between rows
                for (int k = 0; k < board[i].length; k++) {
                    System.out.print("----");
                }
                System.out.println();
            }
        }
    }

    /**
     * printInitialNumbers prints the current game setting
     * (initialNumbers) for debugging.
     */
    public void printInitialNumbers() {
        System.out.println("\nInitial Numbers State:\n");

        for (int i = 0; i < initialNumbers.length; i++) {
            for (int j = 0; j < initialNumbers[i].length; j++) {
                System.out.printf("%4d", initialNumbers[i][j]);
            }
            System.out.println(); // New line at the end of each row
            if (i < initialNumbers.length - 1) {
                // Print a separator line between rows
                for (int k = 0; k < initialNumbers[i].length; k++) {
                    System.out.print("----");
                }
                System.out.println();
            }
        }
    }

}
