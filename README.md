<a name="readme-top"></a>

# JavaFX Player

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#requirements">Requirements</a></li>
      </ul>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li><a href="#features">Features</a></li>
    <li><a href="#components-used-in-the-project">Components Used In The Project</a></li>
    <li><a href="#user-manual">User Manual</a></li>
    <li><a href="#screenshots">Screenshots</a></li>
    <li><a href="#project-setup">Project Setup</a></li>
  </ol>
</details>

## About The Project

Simple JavaFX Multimedia Player.\
Includes jlink and shade plugins and resources for creating native windows installer using jpackage.

### Requirements

* JDK 21
* Maven
* WiX Toolset v3 (for creating native installer with jpackage)

### Built With

* [![JavaFX][JavaFX.com]][JavaFX-url]
* [![Eclipse][Eclipse.com]][Eclipse-url]
* [![GitHub][GitHub.com]][GitHub-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Features

- Supports playback of video files (.mp4 - H.264, .flv) and audio files (.mp3, .m4a - not ALAC).
- Provides multiple controls for managing media files.
- Displays album cover images for music files (or a default image if the album cover is not found).
- Stores the last playback position of media files (up to 100 entries) for resuming playback later, with a 5-second rewind for user convenience.
- Allows media files to be opened using this app.
- The app's window remains on top of other windows.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Components Used In The Project:

- Java 21
- JavaFX 23-ea+22
- JAudiotagger 3.0.1
- Maven
- Git

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## User Manual

Functionalities that are not obvious from the GUI:

- Double-click or press `F11` to toggle between full-screen and windowed mode.
- Press `Esc` to exit full-screen mode.
- Single-click to toggle the visibility of the controls bar.
- Press the `Up arrow` key to show the controls bar.
- Press the `Down arrow` key to hide the controls bar.
- Press the `Left arrow` key to rewind playback by 1 minute.
- Press the `Right arrow` key to fast forward playback by 1 minute.
- Press the `Space` bar to toggle between `PLAYING` and `PAUSED` status or to restart the media after it has finished.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Screenshots

### Start Screen:

![start-screen]

### Paused Video With Visible Controls Bar:

![paused-video-with-visible-controls-bar]

### Paused Video With Visible Controls Bar And Volume Slider:

![paused-video-with-visible-controls-bar-and-volume-slider]

### Paused Video With Hidden Controls Bar:

![paused-video-with-hidden-controls-bar]

### Paused Video With Visible Controls Bar In Full-Screen Mode:

![paused-video-with-visible-controls-bar-in-full-screen-mode]

### Paused Audio With Visible Controls Bar:

![paused-audio-with-visible-controls-bar]

### Paused Audio With Visible Controls Bar And Default Album Cover Image:

![paused-audio-with-visible-controls-bar-and-default-album-cover-image]

### Finished Audio With Visible Controls Bar:

![finished-audio-with-visible-controls-bar]

### Unsupported Media Format:

![unsupported-media-format]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

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
	--vendor "Alex Inc."
	--win-shortcut
	--win-dir-chooser
$ jpackage -t exe --name "FX Player" --app-version 1.0 --input jpackage/input --main-jar fxplayer-0.0.1-SNAPSHOT-shaded.jar --dest jpackage/output_installer --icon jpackage/icon/player.ico --module-path D:/Games/Java/javafx-jmods-23 --add-modules javafx.controls,javafx.graphics,javafx.fxml,javafx.base,javafx.media,java.logging --vendor "Alex Inc." --win-shortcut --win-dir-chooser
```

The `java.logging` module was added due to the `jaudiotagger` dependency. Without this module, an exception will be thrown in the thread `JavaFX Application Thread`, resulting in a `java.lang.NoClassDefFoundError: java/util/logging/Logger at org.jaudiotagger.audio.AudioFileIO.<clinit>(AudioFileIO.java:95)`.

The `--win-console` option can be used to keep the console window attached to your application, allowing you to view the console output.

***

### 3+. Create custom runtime image (including .bat launcher) using javafx-maven-plugin. Launcher path = "target\fxplayer\bin\fxplayer-launcher.bat"
```
$ mvn clean javafx:jlink
```
<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->

[JavaFX.com]: https://img.shields.io/badge/javafx-%23FF0000.svg?style=for-the-badge&logo=javafx&logoColor=white

[JavaFX-url]: https://openjfx.io/

[Eclipse.com]: https://img.shields.io/badge/Eclipse-FE7A16.svg?style=for-the-badge&logo=Eclipse&logoColor=white

[Eclipse-url]: https://www.eclipse.org/

[GitHub.com]: https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white

[GitHub-url]: https://github.com/

[start-screen]:project-info/start_screen.png

[paused-video-with-visible-controls-bar]:project-info/paused_video_with_visible_controls_bar.png

[paused-video-with-visible-controls-bar-and-volume-slider]:project-info/paused_video_with_visible_controls_bar_and_volume_slider.png

[paused-video-with-hidden-controls-bar]:project-info/paused_video_with_hidden_controls_bar.png

[paused-video-with-visible-controls-bar-in-full-screen-mode]:project-info/paused_video_with_visible_controls_bar_in_full_screen_mode.png

[paused-audio-with-visible-controls-bar]:project-info/paused_audio_with_visible_controls_bar.png

[paused-audio-with-visible-controls-bar-and-default-album-cover-image]:project-info/paused_audio_with_visible_controls_bar_and_default_album_cover_image.png

[finished-audio-with-visible-controls-bar]:project-info/finished_audio_with_visible_controls_bar.png

[unsupported-media-format]:project-info/unsupported_media_format.png
