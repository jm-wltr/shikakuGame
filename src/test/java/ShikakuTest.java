package org.cis1200.shikaku;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * File to test implementation.
 */

public class GameTest {

    @Test
    public void testDefaultConstructorInitializes5x5Board() {
        Shikaku game = new Shikaku();
        assertEquals(5, game.getSize());
        assertFalse(game.isGameOver());
        assertEquals(1, game.getCurrentRectID());
    }

    @Test
    public void testResetInitializesBoardCorrectly() {
        Shikaku game = new Shikaku();
        game.reset(10);
        assertEquals(10, game.getSize());
        assertFalse(game.isGameOver());
        assertEquals(1, game.getCurrentRectID());
    }

    @Test
    public void testInitialNumbersGeneratedCorrectlyForDifferentSizes() {
        Shikaku smallGame = new Shikaku();
        smallGame.reset(5);
        assertNotNull(smallGame.getInitialNumbers());
        assertEquals(5, smallGame.getInitialNumbers().length);

        Shikaku largeGame = new Shikaku();
        largeGame.reset(10);
        assertNotNull(largeGame.getInitialNumbers());
        assertEquals(10, largeGame.getInitialNumbers().length);
    }

    @Test
    public void testValidRectanglePlacement() {
        Shikaku game = new Shikaku();
        game.reset(3);
        assertTrue(game.playTurn(0, 0, 1, 1), "Valid rectangle placement should return true");
        assertEquals(2, game.getCurrentRectID(), "Rectangle ID should increment after placement");
    }

    @Test
    public void testInvalidRectangleOverlap() {
        Shikaku game = new Shikaku();
        game.reset(3);
        game.playTurn(0, 0, 1, 1);
        assertFalse(
                game.playTurn(1, 1, 2, 2),
                "Overlapping rectangle placement should return false"
        );
    }

    @Test
    public void testInvalidRectangleOutOfBounds() {
        Shikaku game = new Shikaku();
        game.reset(3);
        assertFalse(
                game.playTurn(2, 2, 3, 3),
                "Rectangle placement out of bounds should return false"
        );
    }

    @Test
    public void testIncorrectRectangleSize() {
        Shikaku game = new Shikaku();
        game.reset(3);
        game.playTurn(0, 0, 1, 0); // Assuming initial numbers don't match this size
        assertFalse(game.isGameOver(), "Game should not end with incorrect rectangle sizes");
    }

    @Test
    public void testUndoAfterRectanglePlacement() {
        Shikaku game = new Shikaku();
        game.reset(3);
        game.playTurn(0, 0, 1, 1);
        game.undo();
        assertEquals(0, game.getCellValue(1, 1), "Board should revert to initial state after undo");
    }

    @Test
    public void testRedoAfterUndo() {
        Shikaku game = new Shikaku();
        game.reset(3);
        game.playTurn(0, 0, 1, 1);
        game.undo();
        game.redo();
        assertEquals(1, game.getCellValue(1, 1), "Board should restore rectangle after redo");
    }

    @Test
    public void testUndoRedoWithEmptyLists() {
        Shikaku game = new Shikaku();
        game.reset(3);
        game.undo();
        game.redo();
        assertEquals(
                0, game.getCellValue(0, 0),
                "Undo/Redo with empty lists should not change the board"
        );
    }

    @Test
    public void testSingleCellGame() {
        Shikaku game = new Shikaku();
        game.reset(1);
        assertTrue(game.playTurn(0, 0, 0, 0), "Placement in a single cell should be valid");
        assertTrue(game.isGameOver(), "Game should be over after filling the only cell");
    }

    @Test
    public void testInvalidTurnOverlap() {
        Shikaku game = new Shikaku();
        game.reset(3); // Resetting to a 3x3 board
        assertTrue(game.playTurn(0, 0, 1, 1), "First move should be valid");
        assertFalse(game.playTurn(1, 1, 2, 2), "Overlap move should be invalid");
        assertFalse(game.isGameOver(), "Game should not be over");
    }

    @Test
    public void testGameReset() {
        Shikaku game = new Shikaku();
        game.reset(3);
        game.playTurn(0, 0, 0, 0);
        game.reset(3);
        assertEquals(0, game.getCellValue(0, 0), "Board should be cleared after reset");
    }

}
