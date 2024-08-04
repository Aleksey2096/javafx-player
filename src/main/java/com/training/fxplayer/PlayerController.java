package com.training.fxplayer;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
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

	// Delay to distinguish between single and double click
	private static final PauseTransition PAUSE_TRANSITION = new PauseTransition(Duration.millis(200));
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
	private Slider volumeSlider;
	@FXML
	private Button playButton;
	@FXML
	private Button volumeButton;
	@FXML
	private Button replay10Button;
	@FXML
	private Button forward30Button;
	@FXML
	private Button openFileButton;
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
	private ImageView ivForward30;
	private ImageView ivReplay10;
	private ImageView ivOpenFile;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		DoubleProperty widthProperty = mediaView.fitWidthProperty();
		DoubleProperty hightProperty = mediaView.fitHeightProperty();

		widthProperty.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
		hightProperty.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

		mediaView.setPreserveRatio(true);

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

		replay10Button.setGraphic(ivReplay10);
		replay10Button.setDisable(true);

		forward30Button.setGraphic(ivForward30);
		forward30Button.setDisable(true);

		volumeButton.setGraphic(ivVolume);
		volumeButton.setDisable(true);

		openFileButton.setGraphic(ivOpenFile);

		volumeSlider.setVisible(false);
		volumeSlider.setManaged(false);

		progressSlider.setDisable(true);
	}

	@FXML
	public void handleOpenFileButtonClick(ActionEvent event) {
		File file = FILE_CHOOSER.showOpenDialog(Player.getPrimaryStage());

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
			progressSlider.setDisable(false);
			replay10Button.setDisable(false);
			forward30Button.setDisable(false);

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

	// on mouse click on play/pause/restart button <.setOnMouseClicked(event ->
	// {...});>
	@FXML
	public void handlePlayButtonClick(ActionEvent event) {
		handlePlayButtonClick();
	}

	// <.setOnMouseClicked(event -> {...});>
	@FXML
	public void handleForward30ButtonClick(ActionEvent event) {
		mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(30)));
	}

	// <.setOnMouseClicked(event -> {...});>
	@FXML
	public void handleReplay10ButtonClick(ActionEvent event) {
		mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(-10)));
	}

	// on mouse click on volume/mute button <.setOnMouseClicked(event -> {...});>
	@FXML
	public void handleVolumeButtonClick(ActionEvent event) {
		if (isMuted) {
			volumeButton.setGraphic(ivVolume);
			volumeSlider.setValue(previousVolume);
			isMuted = false;
		} else {
			volumeButton.setGraphic(ivMute);
			volumeSlider.setValue(0);
			isMuted = true;
		}
	}

	// <.setOnMouseEntered(event -> {...});>
	@FXML
	public void handleMouseEnteringVolumeButton(MouseEvent event) {
		if (!volumeSlider.isVisible()) {
			volumeSlider.setVisible(true);
			volumeSlider.setManaged(true);
		}
	}

	// <.setOnMouseExited(event -> {...});>
	@FXML
	public void handleMouseExitingVolumeControlsHBox(MouseEvent event) {
		volumeSlider.setVisible(false);
		volumeSlider.setManaged(false);
	}

	// <.setOnMousePressed(event -> {...});> and <.setOnMouseDragged(event ->
	// {...});>
	@FXML
	public void handleProgressSliderOnMousePressedAndDragged(MouseEvent event) {
		mediaPlayer.seek(Duration.seconds(progressSlider.getValue()));

		// to prevent the media from automatically playing if it has already ended
		if (isEndOfMedia) {
			mediaPlayer.pause();
		}
	}

	// <.setOnMouseClicked(event -> {...});>
	@FXML
	public void handleStackPaneClick(MouseEvent event) {
		if (event.getClickCount() == 1) {
			// Handle single click with a delay
			PAUSE_TRANSITION.setOnFinished(e -> {
				toggleControlsBarVisibility();
			});
			PAUSE_TRANSITION.play();
		} else if (event.getClickCount() == 2) {
			// Handle double click immediately
			PAUSE_TRANSITION.stop(); // Stop the pause transition if a double click is detected
			Player.toggleFullScreen();
		}
	}

	public void handleKeyPresses() {
		stackPane.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
			switch (keyEvent.getCode()) {
			case F11 -> Player.toggleFullScreen();
			case UP -> setControlsBarVisibility(true);
			case DOWN -> setControlsBarVisibility(false);
			case LEFT -> adjustPlaybackBySeconds(-60);
			case RIGHT -> adjustPlaybackBySeconds(60);
			case SPACE -> handlePlayButtonClick();
			default -> {
			}
			}
		});
	}

	private void bindControlsImages() {

		// Get the paths of the images and make them into images.

		// Button play image
		Image imagePlay = new Image(
				new File("src/main/resources/com/training/fxplayer/img/play.png").toURI().toString());
		ivPlay = new ImageView(imagePlay);
		ivPlay.setFitWidth(25);
		ivPlay.setFitHeight(25);

		// Button pause image.
		Image imagePause = new Image(
				new File("src/main/resources/com/training/fxplayer/img/pause.png").toURI().toString());
		ivPause = new ImageView(imagePause);
		ivPause.setFitHeight(25);
		ivPause.setFitWidth(25);

		// Button restart image.
		Image imageRestart = new Image(
				new File("src/main/resources/com/training/fxplayer/img/restart.png").toURI().toString());
		ivRestart = new ImageView(imageRestart);
		ivRestart.setFitWidth(25);
		ivRestart.setFitHeight(25);

		// Button mute image.
		Image imageMute = new Image(
				new File("src/main/resources/com/training/fxplayer/img/mute.png").toURI().toString());
		ivMute = new ImageView(imageMute);
		ivMute.setFitWidth(25);
		ivMute.setFitHeight(25);

		// Button volume image.
		Image imageVolume = new Image(
				new File("src/main/resources/com/training/fxplayer/img/volume.png").toURI().toString());
		ivVolume = new ImageView(imageVolume);
		ivVolume.setFitWidth(25);
		ivVolume.setFitHeight(25);

		// Button forward_30 image.
		Image imageForward30 = new Image(
				new File("src/main/resources/com/training/fxplayer/img/forward30.png").toURI().toString());
		ivForward30 = new ImageView(imageForward30);
		ivForward30.setFitWidth(25);
		ivForward30.setFitHeight(25);

		// Button replay_10 image.
		Image imageReplay10 = new Image(
				new File("src/main/resources/com/training/fxplayer/img/replay10.png").toURI().toString());
		ivReplay10 = new ImageView(imageReplay10);
		ivReplay10.setFitWidth(25);
		ivReplay10.setFitHeight(25);

		// Button open_file image.
		Image imageOpenFile = new Image(
				new File("src/main/resources/com/training/fxplayer/img/openFile.png").toURI().toString());
		ivOpenFile = new ImageView(imageOpenFile);
		ivOpenFile.setFitWidth(25);
		ivOpenFile.setFitHeight(25);
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

	private void setControlsBarVisibility(boolean isVisible) {
		controlsBox.setVisible(isVisible);
		controlsBox.setManaged(isVisible);
	}

	private void adjustPlaybackBySeconds(int seconds) {
		if (mediaPlayer != null) {
			mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(seconds)));
		}
	}

	private void handlePlayButtonClick() {
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
}
