package project;

/*
 * Shikaku Game
 * Adapted from my final project for CIS 1200 at the University of Pennsylvania
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * RunShikaku sets up the GUI for the Shikaku game.
 *
 * This implementation adheres to a Model-View-Controller (MVC) design
 * framework,
 * which is particularly effective for turn-based games. The MVC pattern
 * separates
 * the game logic (model) from the user interface (view), with a controller
 * to manage interactions between the two.
 *
 * We recommend reviewing the MVC design framework for a deeper understanding:
 * https://www.seas.upenn.edu/~cis120/current/files/slides/lec37.pdf
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

public class RunShikaku implements Runnable {

    @Override
    public void run() {
        // Initialize the main frame
        final JFrame frame = new JFrame("Shikaku");
        frame.setLocation(100, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize the timer label
        JLabel timerLabel = new JLabel("00:00:00");

        // Initialize the game board
        final ShikakuBoard board = new ShikakuBoard(timerLabel);
        frame.add(board, BorderLayout.CENTER);

        // Initialize Undo and Redo buttons and their panel
        JButton undoButton = new JButton("Undo");
        JButton redoButton = new JButton("Redo");
        JPanel undoRedoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        undoRedoPanel.add(undoButton);
        undoRedoPanel.add(redoButton);

        // Set up action listeners for Undo and Redo buttons
        undoButton.addActionListener(e -> board.undo());
        redoButton.addActionListener(e -> board.redo());

        // Panel for timer and undo/redo group
        JPanel timerUndoRedoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        timerUndoRedoPanel.add(timerLabel);
        timerUndoRedoPanel.add(undoRedoPanel);

        // Initialize the tools panel and add components
        final JPanel tools_panel = new JPanel();
        tools_panel.add(timerUndoRedoPanel);
        frame.add(tools_panel, BorderLayout.SOUTH);

        // Initialize the Instructions button
        final JButton instructionsButton = new JButton("Instructions");
        tools_panel.add(instructionsButton);
        instructionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String instructions = "How to Play Shikaku:\n" +
                        "- Divide the grid into rectangles.\n" +
                        "- Each section contains one number.\n" +
                        "- Area of each section equals the number.\n\n" +
                        "Controls:\n" +
                        "- Create: Click on starting corner, then \n" +
                        "click on second corner to confirm rectangle.\n" +
                        "- Delete: Click on a rectangle.\n" +
                        "- Undo/Redo: Use Undo and Redo buttons.\n" +
                        "- Restart: Select new game size.\n\n" +
                        "Enjoy the puzzle!";
                JOptionPane.showMessageDialog(
                        frame, instructions,
                        "Instructions", JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        // Initialize the game size buttons and their panel
        final JPanel reset_panel = new JPanel();
        final JLabel reset_label = new JLabel("Start a new game: ");
        reset_panel.add(reset_label);

        JButton size5 = new JButton("Easy");
        JButton size10 = new JButton("Medium");
        JButton size15 = new JButton("Hard");
        JButton size30 = new JButton("Extreme");

        reset_panel.add(size5);
        reset_panel.add(size10);
        reset_panel.add(size15);
        reset_panel.add(size30);

        // Set up action listeners for game size buttons
        size5.addActionListener(e -> board.newGame(5));
        size10.addActionListener(e -> board.newGame(10));
        size15.addActionListener(e -> board.newGame(15));
        size30.addActionListener(e -> board.newGame(30));

        frame.add(reset_panel, BorderLayout.NORTH);

        // Finalize frame setup and start the game
        frame.pack();
        frame.setVisible(true);
        board.newGame(5);
    }
}
