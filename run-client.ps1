<#
Updated PowerShell script to compile and run the JavaFX client with Jackson support.
#>

# === Path Settings ===
$javafx = 'C:\Program Files\Java\javafx-sdk-21.0.9\lib'
$clientDir = 'client'
$libDir = "$clientDir\lib"  # where your Jackson JARs are
$out = 'out'

Write-Host "Using JavaFX lib: $javafx"

if (-not (Test-Path $javafx)) {
    Write-Error "JavaFX lib folder not found: $javafx"
    exit 1
}
if (-not (Test-Path $libDir)) {
    Write-Warning "⚠️  Library folder not found: $libDir (expected to contain jackson-*.jar)"
}

New-Item -ItemType Directory -Force -Path $out | Out-Null

# === Compile step ===
$src = Get-ChildItem -Path "$clientDir/src/main/java" -Recurse -Filter '*.java' | ForEach-Object { $_.FullName }
if ($src.Count -eq 0) { Write-Error "No Java source files found"; exit 1 }

Write-Host "Compiling $($src.Count) Java files..."

# Combine JavaFX + lib jars into one classpath
$classpath = "$($javafx)\*;$($libDir)\*"

$modules = 'javafx.controls,javafx.fxml'

javac --module-path "$javafx" --add-modules $modules `
-classpath "$classpath" `
-d $out $src

if ($LASTEXITCODE -ne 0) {
    Write-Error "javac failed (see errors above)"
    exit $LASTEXITCODE
}

# === Copy resources (FXML, CSS) ===
$resourcesRoot = "$clientDir/src/main/resources"
if (Test-Path $resourcesRoot) {
    Write-Host "Copying resources..."
    Copy-Item -Path (Join-Path $resourcesRoot '*') -Destination $out -Recurse -Force
}
else {
    Write-Warning "No resources folder found at $resourcesRoot"
}

# === Run step ===
Write-Host "Running client..."
java --enable-native-access=javafx.graphics `
--module-path "$javafx" --add-modules $modules `
-classpath "$out;$($javafx)\*;$($libDir)\*" `
client.QuizClient
