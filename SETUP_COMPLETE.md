# Frontend JavaFX Client - Quick Start Guide

## ✅ Fixed: JavaFX Dependencies

I've added Maven support to the frontend project to properly manage JavaFX dependencies.

## What Was Done

1. **Created `pom.xml`** - Maven configuration with JavaFX dependencies
2. **Updated `.vscode/settings.json`** - Configured for automatic Maven integration
3. **Downloaded JavaFX libraries** - Maven has already downloaded all required JARs
4. **Updated README.md** - Added new Maven-based run instructions

## 🔧 Fix VS Code Errors (One-Time Setup)

The red squiggly lines you're seeing are because VS Code's Java extension hasn't reloaded yet. Here's how to fix it:

### Method 1: Reload VS Code Window (Easiest)
1. Press `Ctrl+Shift+P` (or `F1`)
2. Type: `Developer: Reload Window`
3. Press Enter
4. Wait 10-20 seconds for Java extension to reload
5. Errors should disappear! ✨

### Method 2: Close and Reopen VS Code
1. Close VS Code completely
2. Reopen the workspace
3. Wait for Java extension to initialize

## 🚀 Running the Frontend

Now you can run the client using Maven (no need for manual JavaFX SDK setup!):

### Quick Run
```powershell
cd C:\Users\Sashini\NPAssignment\Frontend\network-programming
mvn javafx:run
```

### Alternative: Compile then Run
```powershell
mvn clean compile exec:java
```

## ✅ Verify It's Working

After reloading VS Code:
1. Open `QuizClient.java`
2. JavaFX imports should have no errors
3. Hover over `Application` - should show JavaFX documentation
4. Run `mvn javafx:run` to launch the app

## 📁 What Changed

- ✅ `pom.xml` - New Maven configuration with JavaFX 21.0.1
- ✅ `.vscode/settings.json` - Updated for automatic Maven classpath
- ✅ `README.md` - Updated with Maven instructions
- ✅ Maven dependencies downloaded to local repository

## 🎯 Next Steps

1. **Reload VS Code window** (Ctrl+Shift+P → "Developer: Reload Window")
2. Wait for Java extension to finish loading (check bottom-right status bar)
3. Verify no errors in `QuizClient.java`
4. Run the client: `mvn javafx:run`

---

**Note**: The old `run-client.ps1` script still works if you prefer manual JavaFX SDK, but Maven is much easier and handles everything automatically!
