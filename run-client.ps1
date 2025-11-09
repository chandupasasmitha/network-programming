<#
Simple PowerShell script to compile and run the JavaFX client.

Edit the $javafx variable below if your JavaFX SDK is installed somewhere else.
You provided the path that ends with `.../bin` — the script uses the `lib` folder inside the SDK.
#>

# Path to JavaFX SDK 'lib' folder (change only if different)
$javafx = 'C:/Users/chand/Downloads/openjfx-25.0.1_windows-x64_bin-sdk/javafx-sdk-25.0.1/lib'

$out = 'out'
Write-Host "Using JavaFX lib: $javafx"

if (-not (Test-Path $javafx)) {
    Write-Error "JavaFX lib folder not found: $javafx`nMake sure you set the path to the SDK's 'lib' directory (contains javafx-controls.jar, javafx-fxml.jar, ...)."
    exit 1
}

New-Item -ItemType Directory -Force -Path $out | Out-Null

# Collect all Java source files from client only (models are inside client now)
$src = Get-ChildItem -Path 'client/src/main/java' -Recurse -Filter '*.java' | ForEach-Object { $_.FullName }
if ($src.Count -eq 0) { Write-Error "No Java sources found under client/src/main/java"; exit 1 }

Write-Host "Compiling $($src.Count) Java files..."
javac --module-path "$javafx" --add-modules javafx.controls, javafx.fxml -d $out $src
if ($LASTEXITCODE -ne 0) { Write-Error "javac failed (see messages above)"; exit $LASTEXITCODE }

Write-Host "Running client..."
# Copy resources (FXML, CSS, etc.) to output so FXMLLoader can find them on the classpath
$resourcesRoot = 'client/src/main/resources'
if (Test-Path $resourcesRoot) {
    Write-Host "Copying resources from $resourcesRoot to $out"
    Copy-Item -Path (Join-Path $resourcesRoot '*') -Destination $out -Recurse -Force
}
else {
    Write-Warning "No resources folder found at $resourcesRoot"
}

java --enable-native-access=javafx.graphics --module-path "$javafx" --add-modules javafx.controls, javafx.fxml -cp $out client.QuizClient
