<div style="display: flex; align-items: center;">
  <img src="https://github.com/user-attachments/assets/500103cb-bfa4-4880-bef2-86071d4f9cdf" alt="Shikaku" style="height: 100px; margin-right: 10px;"> 
<h1 style="margin: 0;">Shikaku Game</h1>
</div>


Shikaku (also known as "Divide by Squares") is a fun and challenging Japanese logic-based puzzle game, similar to the more popular Sudoku.
The goal of the game is to divide a grid into rectangular or square sections such that each section contains exactly one number, and that number represents the area of the section.
This implementation is done with Java Swing. I originally did it as final project for CIS 1200 at University of Pennsylvania in Fall 2023, and I have recently been improving it.
Also feel free to clone and fork this repository. I would love to see new contributions or receive suggestions.

I have made a **Github release**, which can be found on this repository, including the files to download the game as an app in different operating systems (.exe, .app, .jar).

---
### Prerequisites

- Java Development Kit (JDK 8 or later)
- Any Java IDE (e.g., IntelliJ IDEA, Eclipse, or VSCode with Java support)
o start the game.

Note the Github Releases version for windows does not require Java nor an IDE, as it has a JRE bundled.

---

## How to Play

1. Each number on the grid represents the area of the rectangle or square it belongs to.
2. Draw rectangles by clicking and dragging over the grid.
3. Make sure each rectangle:
   - Contains only one number.
   - Has an area equal to the number inside it.
4. Solve the puzzle by dividing the entire grid correctly.

---

## Project Structure

```plaintext
ShikakuGame/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── project/
│   │   │   │   ├── Game.java           # Entry point of the application
│   │   │   │   ├── RunShikaku.java     # Sets up the GUI
│   │   │   │   ├── Shikaku.java        # Model for game logic
│   │   │   │   └── ShikakuBoard.java   # 'View' and 'Controller', handling the game's visuals
│   │   └── resources/
│   │       ├── shikaku_mac.png/        # App icon used for macOS
│   │       └── shikaku_win.png/        # App icon used for Windows
│   └── test/
│       └── java/
│           └── project/
│               └── ShikakuTest.java    # Unit tests for game logic
├── README.md                           # Project documentation
└── .gitignore                          # Ignored files
```

---

## Contributing

Contributions are welcome! If you have suggestions for improving the game or want to add new features, feel free to:

1. Fork the repository.
2. Create a new branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add your feature description"
   ```
4. Push to your branch:
   ```bash
   git push origin feature/your-feature-name
   ```
5. Open a Pull Request.

---

## License

This project is licensed under the [MIT License](LICENSE).

---

## Acknowledgments

- Inspired by the classic Shikaku puzzles.
- Adapted from my final project for CIS 1200 at the University of Pennsylvania

---

## Screenshot

<img width="400" alt="Captura de pantalla 2025-01-18 a las 3 33 42 p  m" src="https://github.com/user-attachments/assets/f76f5ec9-c1a6-4e81-919a-dd83750147f7" />
