# Multiplayer Quiz Game (Client - JavaFX)

This workspace contains the JavaFX frontend for the Multiplayer Quiz Game. It is a lightweight scaffold with the following features:

- `client/src/main/java/client/QuizClient.java` — JavaFX Application entry point (loads `Lobby.fxml`).
- Controllers: Lobby, Question, Leaderboard, Admin (basic UI handlers; socket/REST integration TODO).
- Models: `Player`, `Question`, `Score`.
- FXML views under `client/src/main/resources/fxml/` and a `styles.css` file.

## Prerequisites

- **JDK 21** (or Java 11+)
- **Maven** (for dependency management and building)

## Running the Client

### Option 1: Using Maven JavaFX Plugin (Recommended)

The easiest way to run the JavaFX client:

```powershell
cd C:\Users\Sashini\NPAssignment\Frontend\network-programming
mvn clean javafx:run
```

This automatically handles JavaFX dependencies and module paths.

### Option 2: Using Maven Exec Plugin

```powershell
mvn clean compile exec:java
```

### Option 3: Using the PowerShell Script (Manual JavaFX SDK)

If you prefer to use a locally installed JavaFX SDK:

1. Download JavaFX SDK from [openjfx.io](https://openjfx.io/)
2. Edit `run-client.ps1` and set the `$javafx` variable to your JavaFX SDK `lib` folder
3. Run:

```powershell
.\run-client.ps1
```

## VS Code Setup

If you see JavaFX import errors in VS Code:

1. Make sure the Java Extension Pack is installed
2. Run Maven to download dependencies:
   ```powershell
   mvn clean compile
   ```
3. Reload VS Code window: Press `Ctrl+Shift+P` → type "Developer: Reload Window" → press Enter
4. The errors should disappear once Maven resolves the JavaFX dependencies

## Project Structure

```
Frontend/network-programming/
├── client/
│   └── src/
│       └── main/
│           ├── java/client/           # Java source files
│           │   ├── QuizClient.java
│           │   ├── AdminController.java
│           │   ├── LeaderboardController.java
│           │   ├── LobbyController.java
│           │   └── QuestionController.java
│           └── resources/             # FXML and CSS files
│               ├── css/styles.css
│               └── fxml/
│                   ├── AdminDashboard.fxml
│                   ├── Leaderboard.fxml
│                   ├── Lobby.fxml
│                   └── Question.fxml
├── pom.xml                            # Maven configuration
├── run-client.ps1                     # Alternative run script
└── README.md                          # This file
```

## Troubleshooting

### JavaFX Import Errors in VS Code

**Error**: `package javafx.application does not exist`

**Solution**:
1. Run `mvn clean compile` to download dependencies
2. Reload VS Code window: `Ctrl+Shift+P` → "Developer: Reload Window"
3. Check that `pom.xml` exists and contains JavaFX dependencies
4. Verify Java extension is using the project's classpath

### Application Won't Start

**Error**: `Error: JavaFX runtime components are missing`

**Solution**:
- Use Maven commands (`mvn javafx:run` or `mvn exec:java`) which handle JavaFX modules automatically
- If using manual `java` command, ensure `--module-path` and `--add-modules` are specified

### FXML Not Found

**Error**: `Location is not set` or FXML file not found

**Solution**:
- Ensure resources are in `client/src/main/resources/`
- Check FXML paths start with `/` (e.g., `/fxml/Lobby.fxml`)
- Run `mvn clean compile` to copy resources to target

## Notes and Next Steps

- The UI buttons are placeholders for navigation; when the server and REST APIs are ready, wire the controllers to real network calls.
- JavaFX dependencies are now managed by Maven for easier setup and IDE integration.
