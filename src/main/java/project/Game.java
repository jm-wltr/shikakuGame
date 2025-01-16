package project;

import javax.swing.*;

public class Game {
    /**
     * Main method run to start and run the game. Initializes the runnable game
     * class and runs it. IMPORTANT: Do NOT delete!
     */
    public static void main(String[] args) {
        // Set the game you want to run here
        Runnable game = new project.RunShikaku();

        SwingUtilities.invokeLater(game);
    }
}
