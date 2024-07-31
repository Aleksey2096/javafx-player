package com.training.fxplayer;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class PlayerController implements Initializable {

	private static final FileChooser FILE_CHOOSER = new FileChooser();

	private MediaPlayer mediaPlayer;
	private boolean isEndOfMedia;
	private double previousVolume;
	private boolean isMuted;

	@FXML
	private VBox controlsBox;
	@FXML
	private StackPane stackPane;
	@FXML
	private MediaView mediaView;
	@FXML
	private Slider progressSlider;
	@FXML
	private Button volumeButton;
	@FXML
	private Slider volumeSlider;
	@FXML
	private Button playButton;
	@FXML
	private Label currentTimeLabel;
	@FXML
	private Label durationLabel;
	@FXML
	private HBox volumeControlsHBox;

	// ImageViews for the buttons and labels.
	private ImageView ivPlay;
	private ImageView ivPause;
	private ImageView ivRestart;
	private ImageView ivVolume;
	private ImageView ivMute;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		DoubleProperty widthProperty = mediaView.fitWidthProperty();
		DoubleProperty hightProperty = mediaView.fitHeightProperty();

		widthProperty.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
		hightProperty.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

		mediaView.setPreserveRatio(true);

		stackPane.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				Player.toggleFullScreen();
			} else if (event.getClickCount() == 1) {
				toggleControlsBarVisibility();
			}
		});

		EventHandler<MouseEvent> changedProgressBar = event -> {
			mediaPlayer.seek(Duration.seconds(progressSlider.getValue()));

			// to prevent the media from automatically playing if it has already ended
			if (isEndOfMedia) {
				mediaPlayer.pause();
			}
		};

		progressSlider.setOnMousePressed(changedProgressBar);
		progressSlider.setOnMouseDragged(changedProgressBar);

		volumeSlider.valueProperty().addListener(observable -> {
			if (mediaPlayer != null) {
				double newVolume = volumeSlider.getValue();
				mediaPlayer.setVolume(newVolume);

				if (newVolume != 0.0) {
					volumeButton.setGraphic(ivVolume);
					previousVolume = newVolume;
					isMuted = false;
				} else {
					volumeButton.setGraphic(ivMute);
					isMuted = true;
				}
			}
		});

		volumeButton.setOnMouseClicked(event -> {
			if (isMuted) {
				volumeButton.setGraphic(ivVolume);
				volumeSlider.setValue(previousVolume);
				isMuted = false;
			} else {
				volumeButton.setGraphic(ivMute);
				volumeSlider.setValue(0);
				isMuted = true;
			}
		});

		volumeButton.setOnMouseEntered(event -> {
			if (!volumeSlider.isVisible()) {
				volumeSlider.setVisible(true);
				volumeSlider.setManaged(true);
			}
		});

		volumeControlsHBox.setOnMouseExited(event -> {
			volumeSlider.setVisible(false);
			volumeSlider.setManaged(false);
		});

		bindControlsImages();

		/*
		 * SET THE DEFAULTS
		 */

		previousVolume = 0.7;
		isMuted = false;
		volumeSlider.setValue(previousVolume);

		playButton.setGraphic(ivPlay);
		playButton.setDisable(true);
		isEndOfMedia = false;

		volumeButton.setGraphic(ivVolume);
		volumeButton.setDisable(true);

		volumeSlider.setVisible(false);
		volumeSlider.setManaged(false);
	}

	@FXML
	public void chooseFile(ActionEvent event) {
		File file = FILE_CHOOSER.showOpenDialog(null);

		if (file != null) {
			Media media = new Media(file.toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			mediaView.setMediaPlayer(mediaPlayer);

			mediaPlayer.setOnReady(() -> {
				progressSlider.setMax(media.getDuration().toSeconds());
			});

			mediaPlayer.setOnEndOfMedia(() -> {
				playButton.setGraphic(ivRestart);
				isEndOfMedia = true;
			});

			Player.setAppTitle(String.format("%s - %s", Player.APP_NAME, file.getName()));
			mediaPlayer.play();
			playButton.setGraphic(ivPause);
			playButton.setDisable(false);
			volumeButton.setGraphic(ivVolume);
			volumeButton.setDisable(false);

			mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
				currentTimeLabel.setText(formatTime(mediaPlayer.getCurrentTime()) + " / ");

				progressSlider.setValue(newValue.toSeconds());

				// changes restart icon to play icon if the user selected another playback time
				// after the media has ended
				if (newValue.toSeconds() < media.getDuration().toSeconds() && isEndOfMedia) {
					playButton.setGraphic(ivPlay);
					isEndOfMedia = false;
				}
			});

			mediaPlayer.totalDurationProperty().addListener((observable, oldValue, newValue) -> {
				durationLabel.setText(formatTime(mediaPlayer.getTotalDuration()));
			});
		}
	}

	@FXML
	public void playPause(ActionEvent event) {
		if (isEndOfMedia) {
			isEndOfMedia = false;
			mediaPlayer.seek(Duration.ZERO);
			mediaPlayer.play();
			playButton.setGraphic(ivPause);
		} else if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
			mediaPlayer.pause();
			playButton.setGraphic(ivPlay);
		} else {
			mediaPlayer.play();
			playButton.setGraphic(ivPause);
		}
	}

	@FXML
	public void skip30(ActionEvent event) {
		mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(30)));
	}

	@FXML
	public void back10(ActionEvent event) {
		mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(-10)));
	}

	private void bindControlsImages() {

		// Get the paths of the images and make them into images.
		Image imagePlay = new Image(
				new File("src/main/resources/com/training/fxplayer/img/play-btn.png").toURI().toString());
		ivPlay = new ImageView(imagePlay);
		ivPlay.setFitWidth(19);
		ivPlay.setFitHeight(19);

		// Button stop image.
		Image imageStop = new Image(
				new File("src/main/resources/com/training/fxplayer/img/stop-btn.png").toURI().toString());
		ivPause = new ImageView(imageStop);
		ivPause.setFitHeight(19);
		ivPause.setFitWidth(19);

		// Restart button image.
		Image imageRestart = new Image(
				new File("src/main/resources/com/training/fxplayer/img/restart-btn.png").toURI().toString());
		ivRestart = new ImageView(imageRestart);
		ivRestart.setFitWidth(19);
		ivRestart.setFitHeight(19);

		// Muted speaker image.
		Image imageMute = new Image(
				new File("src/main/resources/com/training/fxplayer/img/mute.png").toURI().toString());
		ivMute = new ImageView(imageMute);
		ivMute.setFitWidth(19);
		ivMute.setFitHeight(19);

		// Unmuted speaker image.
		Image imageVol = new Image(
				new File("src/main/resources/com/training/fxplayer/img/volume.png").toURI().toString());
		ivVolume = new ImageView(imageVol);
		ivVolume.setFitWidth(19);
		ivVolume.setFitHeight(19);
	}

	private String formatTime(Duration duration) {
		int hours = (int) duration.toHours();
		int minutes = (int) duration.toMinutes() % 60;
		int seconds = (int) duration.toSeconds() % 60;

		if (hours > 0) {
			return String.format("%d:%02d:%02d", hours, minutes, seconds);
		} else {
			return String.format("%d:%02d", minutes, seconds);
		}
	}

	private void toggleControlsBarVisibility() {
		controlsBox.setVisible(!controlsBox.isVisible());
		controlsBox.setManaged(!controlsBox.isManaged());
	}
}
