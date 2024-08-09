package com.training.fxplayer.examples;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Test extends Application {

	private MediaPlayer mediaPlayer;
	private ImageView albumCoverView;
	private MediaView mediaView;
	private StackPane mediaContainer;
	private Label errorMessageLabel;

	@Override
	public void start(Stage primaryStage) {
		VBox root = new VBox();
		Label label = new Label("Choose a media file to play:");
		Button chooseFileButton = new Button("Choose File");

		albumCoverView = new ImageView();
		albumCoverView.setFitWidth(400); // Adjust width
		albumCoverView.setFitHeight(400); // Adjust height

		mediaView = new MediaView();

		// Error message label for unsupported media
		errorMessageLabel = new Label("Unsupported media format.");
		errorMessageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
		errorMessageLabel.setVisible(false); // Initially hidden

		// Use StackPane to hold MediaView, ImageView, and Error Message
		mediaContainer = new StackPane();
		mediaContainer.getChildren().addAll(mediaView, albumCoverView, errorMessageLabel);

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

				try {
					Media media = new Media(file.toURI().toString());
					mediaPlayer = new MediaPlayer(media);
					mediaView.setMediaPlayer(mediaPlayer);

					mediaPlayer.setOnReady(() -> {
						Image albumCover = getAlbumArt(file);

						if (albumCover != null) {
							// If album art is found, show it
							albumCoverView.setImage(albumCover);
							albumCoverView.setVisible(true);
							mediaView.setVisible(false);
							errorMessageLabel.setVisible(false);
						} else {
							// If it's a video file or no album art found
							albumCoverView.setVisible(false);
							mediaView.setVisible(true);
							errorMessageLabel.setVisible(false);
						}

						mediaPlayer.play();
					});

					mediaPlayer.setOnError(() -> {
						displayErrorMessage("Error playing media: " + mediaPlayer.getError().getMessage());
					});
				} catch (MediaException me) {
					displayErrorMessage("Unsupported media format.");
				}
			}
		});

		Scene scene = new Scene(root, 600, 500);
		primaryStage.setTitle("Media Player with JAudiotagger");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void releaseCurrentMediaPlayer() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.dispose();
		}
	}

	private void displayErrorMessage(String message) {
		errorMessageLabel.setText(message);
		errorMessageLabel.setVisible(true);
		mediaView.setVisible(false);
		albumCoverView.setVisible(false);
	}

	private Image getAlbumArt(File file) {
		try {
			AudioFile audioFile = AudioFileIO.read(file);
			Tag tag = audioFile.getTag();
			if (tag != null) {
				Artwork artwork = tag.getFirstArtwork();
				if (artwork != null) {
					byte[] imageData = artwork.getBinaryData();
					return new Image(new ByteArrayInputStream(imageData));
				}
			}
		} catch (Exception e) {
			System.out.println("Error reading album art: " + e.getMessage());
		}
		return null;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
