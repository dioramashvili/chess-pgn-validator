# ♟️ Chess PGN Validator

This is a Java-based PGN (Portable Game Notation) Validator that validates chess games both syntactically and logically. It parses SAN (Standard Algebraic Notation) moves, replays the game according to standard chess rules, and reports detailed errors for any malformed or illegal moves.

---

## 📌 Features

- ✅ PGN header and SAN syntax validation
- ✅ Full chess rules logic: castling, promotion, en passant, check/checkmate/stalemate
- ✅ Detects and reports both syntax and logic errors
- ✅ Supports PGN files with many games (e.g., tournaments)
- ✅ Efficient multithreaded processing
- ✅ Clear error reporting and game state tracking
- ✅ Comprehensive unit tests (valid and invalid scenarios)

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/davit/chess/
│   │       ├── model/            # Pieces, board, move logic
│   │       ├── parser/           # PGN/SAN parsing and validation
│   │       ├── PGNValidatorApp.java      # Main validator
│   │       └── PGNValidatorParallel.java # Multithreaded validator
│   └── resources/
│       └── Tbilisi2015.pgn       # Sample input file
├── test/
│   └── java/
│       └── com/davit/chess/
│           └── model/
│               ├── ChessModelTest.java      # Valid move tests
│               └── InvalidMoveTests.java    # Invalid/syntax test cases
```

---

## ▶️ How to Run

1. Ensure you are using **Java 17+**.
2. Clone or download the project.
3. Compile and run the PGN validator app:

```bash
javac -d out src/main/java/com/davit/chess/**/*.java
java -cp out com.davit.chess.PGNValidatorApp
```

To run the parallel version:

```bash
java -cp out com.davit.chess.parallel.PGNValidatorParallel
```

---

## 🧪 Running Tests

Use a testing tool such as JUnit 5 with your IDE, or compile and run manually:

```bash
javac -cp .:junit-platform-console-standalone.jar -d out src/test/java/com/davit/chess/model/*.java
java -jar junit-platform-console-standalone.jar --class-path out --scan-class-path
```

---

## 📥 Sample Input

Located in: `src/main/resources/Tbilisi2015.pgn`

```
[Event "Tbilisi FIDE GP 2015"]
[Site "Tbilisi GEO"]
[Date "2015.02.25"]
[Round "9.2"]
[White "Jobava,Ba"]
[Black "Vachier Lagrave,M"]
[Result "0-1"]
1. e4 c5 2. Nc3 Nc6 3. Bb5 Nd4 4. a4 a6 5. Be2 g6 ...
```

---

## 📤 Sample Output

```
🎯 Validating Game #1
✅ Game #1 valid (ONGOING)
...
✅ Passed: 65
❌ Failed: 1
📦 Total: 66
```

---

## 👨‍🔬 Author

Created by **Davit Ioramashvili** as part of a chess validation system for analyzing and grading PGN files.

---

## 📄 License

This project is for educational purposes and can be freely used or extended.