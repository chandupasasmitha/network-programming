# Multiplayer Quiz Game (Client - JavaFX)

This workspace contains the JavaFX frontend for the Multiplayer Quiz Game. It is a lightweight scaffold with the following features:

- `client/src/main/java/client/QuizClient.java` — JavaFX Application entry point (loads `Lobby.fxml`).
- Controllers: Lobby, Question, Leaderboard, Admin (basic UI handlers; socket/REST integration TODO).
- Models: `Player`, `Question`, `Score`.
- FXML views under `client/src/main/resources/fxml/` and a `styles.css` file.

How to run (Windows + PowerShell)

1. Make sure you have a Java 11+ JDK and JavaFX SDK installed. If using OpenJDK, install JavaFX separately (OpenJFX).

2. Run the client from the command line. Example (adjust paths for your JavaFX SDK):

```powershell
# Set these paths to where JavaFX is installed on your machine
$javafx = 'C:/javafx/lib'
$cp = 'client/src/main/java'

# Compile
javac --module-path $javafx --add-modules javafx.controls,javafx.fxml -d out $(Get-ChildItem -Path client/src/main/java -Recurse -Include '*.java' | ForEach-Object { $_.FullName })

# Run
java --module-path $javafx --add-modules javafx.controls,javafx.fxml -cp out client.QuizClient
```

Notes and next steps

- The UI buttons are placeholders for navigation; when the server and REST APIs are ready, wire the controllers to real network calls.
- Consider adding a build system (Maven/Gradle) to manage JavaFX dependencies and simplify running.
