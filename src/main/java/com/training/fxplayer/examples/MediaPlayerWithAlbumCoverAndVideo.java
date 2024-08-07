package com.training.fxplayer.examples;

import java.io.File;
import java.util.Map;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MediaPlayerWithAlbumCoverAndVideo extends Application {

	private MediaPlayer mediaPlayer;
	private ImageView albumCoverView;
	private MediaView mediaView;
	private StackPane mediaContainer;

	@Override
	public void start(Stage primaryStage) {
		VBox root = new VBox();
		Label label = new Label("Choose a media file to play:");
		Button chooseFileButton = new Button("Choose File");

		albumCoverView = new ImageView();
		albumCoverView.setFitWidth(400); // Adjust width
		albumCoverView.setFitHeight(400); // Adjust height

		mediaView = new MediaView();

		// Use StackPane to hold both ImageView and MediaView
		mediaContainer = new StackPane();
		mediaContainer.getChildren().addAll(mediaView, albumCoverView);

		root.getChildren().addAll(label, chooseFileButton, mediaContainer);

		chooseFileButton.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();

			// Set the default directory
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

			// Add file filters for both audio and video
			fileChooser.getExtensionFilters().addAll(
					new FileChooser.ExtensionFilter("Audio and Video Files", "*.mp3", "*.wav", "*.aac", "*.mp4",
							"*.mkv", "*.avi"),
					new FileChooser.ExtensionFilter("All Files", "*.*"));

			File file = fileChooser.showOpenDialog(primaryStage);
			if (file != null) {
				releaseCurrentMediaPlayer();

				Media media = new Media(file.toURI().toString());
				mediaPlayer = new MediaPlayer(media);
				mediaView.setMediaPlayer(mediaPlayer);

				mediaPlayer.setOnReady(() -> {
					Map<String, Object> metadata = media.getMetadata();

					// Check if the file is audio or video
					if (metadata.containsKey("image")) {
						// If it's an audio file with an album cover
						albumCoverView.setImage((Image) metadata.get("image"));
						albumCoverView.setVisible(true);
						mediaView.setVisible(false);
					} else {
						// If it's a video file
						albumCoverView.setImage(null);
						albumCoverView.setVisible(false);
						mediaView.setVisible(true);
					}

					mediaPlayer.play();
				});
			}
		});

		Scene scene = new Scene(root, 600, 500);
		primaryStage.setTitle("Media Player with Album Cover and Video");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void releaseCurrentMediaPlayer() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.dispose();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
