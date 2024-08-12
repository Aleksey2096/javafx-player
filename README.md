# Simple JavaFX Multimedia Player 

Includes jlink and shade plugins and resources for creating native windows installer using jpackage.

### Requirements

* Maven
* JDK 21
* WiX Toolset v3 (for creating native installer with jpackage)

## Project Setup

### 1. Clone or download the repository
```
$ git clone https://github.com/Aleksey2096/javafx-player.git
```

### 2. Run application
```
$ mvn clean javafx:run
```

### 3. Create executable fat .jar using maven-shade-plugin. Jar path = "target\fxplayer-0.0.1-SNAPSHOT-shaded.jar"
```
$ mvn install
```

### 4. Move executable jar from "target\fxplayer-0.0.1-SNAPSHOT-shaded.jar" to "jpackage\input\fxplayer-0.0.1-SNAPSHOT-shaded.jar"

### 5. Create native installer file using jpackage and WiX Toolset
```
$ jpackage 
	-t exe 
	--name "FX Player" 
	--app-version 1.0 
	--input jpackage/input 
	--main-jar fxplayer-0.0.1-SNAPSHOT-shaded.jar 
	--dest jpackage/output_installer 
	--icon jpackage/icon/player.ico 
	--module-path D:/Games/Java/javafx-jmods-23 
	--add-modules javafx.controls,javafx.graphics,javafx.fxml,javafx.base,javafx.media,java.logging 
	--win-shortcut
$ jpackage -t exe --name "FX Player" --app-version 1.0 --input jpackage/input --main-jar fxplayer-0.0.1-SNAPSHOT-shaded.jar --dest jpackage/output_installer --icon jpackage/icon/player.ico --module-path D:/Games/Java/javafx-jmods-23 --add-modules javafx.controls,javafx.graphics,javafx.fxml,javafx.base,javafx.media,java.logging --win-shortcut
```

The `java.logging` module was added due to the `jaudiotagger` dependency. Without this module, an exception will be thrown in the thread `JavaFX Application Thread`, resulting in a `java.lang.NoClassDefFoundError: java/util/logging/Logger at org.jaudiotagger.audio.AudioFileIO.<clinit>(AudioFileIO.java:95)`.

The `--win-console` option can be used to keep the console window attached to your application, allowing you to view the console output.

***

### 3+. Create custom runtime image (including .bat launcher) using javafx-maven-plugin. Launcher path = "target\fxplayer\bin\fxplayer-launcher.bat"
```
$ mvn clean javafx:jlink
```