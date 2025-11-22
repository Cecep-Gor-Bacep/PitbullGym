This is OOP Project

Please Donate this project

## Getting started

This project requires Java 21 and the corresponding OpenJFX (JavaFX) SDK. The repository does not include the JavaFX binaries.

1. Download OpenJFX SDK (matching your Java version) from https://openjfx.io or Gluon downloads.
   - Example (Linux, Java 21): download `openjfx-21.0.2_linux-x64_bin-sdk.zip` and extract it.
2. Place the SDK in `lib/javafx-sdk-21.0.2` or anywhere convenient.
3. Compile:

```fish
set FX /path/to/javafx-sdk-21.0.2/lib
javac --module-path $FX --add-modules javafx.controls,javafx.fxml,javafx.swing -encoding UTF-8 -d out src/*.java src/Controller/*.java src/DataAccess/*.java src/Model/*.java
```

4. Run:

```fish
set FX /path/to/javafx-sdk-21.0.2/lib
java --module-path $FX --add-modules javafx.controls,javafx.fxml,javafx.swing -cp out:src Main
```

If you prefer Gradle/Maven automation, I can add a build file to manage JavaFX dependencies.
