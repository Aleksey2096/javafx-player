package com.training.fxplayer;

import static com.training.fxplayer.services.ImageService.extractAlbumCover;
import static com.training.fxplayer.services.TimeFormatterService.formatTime;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import com.training.fxplayer.gui.FileSelector;
import com.training.fxplayer.gui.MediaContainer;
import com.training.fxplayer.gui.MediaContainer.MediaContainerElement;
import com.training.fxplayer.gui.buttons.Forward30Button;
import com.training.fxplayer.gui.buttons.OpenFileButton;
import com.training.fxplayer.gui.buttons.PlayButton;
import com.training.fxplayer.gui.buttons.Replay10Button;
import com.training.fxplayer.gui.buttons.VolumeButton;
import com.training.fxplayer.gui.views.FXPlayerImageView;
import com.training.fxplayer.gui.views.FXPlayerMediaView;

import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class PlayerController implements Initializable {

	private static final double INITIAL_VOLUME = 0.7;
	private static final int LEFT_BUTTON_PLAYBACK_ADJUSTMENT = -60;
	private static final int RIGHT_BUTTON_PLAYBACK_ADJUSTMENT = 60;
	private static final int REPLAY10_BUTTON_PLAYBACK_ADJUSTMENT = -10;
	private static final int FORWARD30_BUTTON_PLAYBACK_ADJUSTMENT = 30;
	private static final String APP_TITLE_FORMAT = "%s - %s";
	private static final String CURRENT_TOTAL_TIME_DELIMITER = " / ";

	// Delay to distinguish between single and double click
	private static final PauseTransition PAUSE_TRANSITION = new PauseTransition(Duration.millis(200));

	private final FileSelector fileSelector = new FileSelector(Player.getPrimaryStage());

	private MediaPlayer mediaPlayer;
	private MediaContainer mediaContainer;
	private boolean isEndOfMedia = false;
	private double previousVolume = INITIAL_VOLUME;
	private boolean isMuted = false;

	private ChangeListener<Duration> currentTimeListener;

	@FXML
	private VBox controlsBox;
	@FXML
	private StackPane stackPane;
	@FXML
	private FXPlayerMediaView mediaView;
	@FXML
	private FXPlayerImageView imageView;
	@FXML
	private Slider progressSlider;
	@FXML
	private Slider volumeSlider;
	@FXML
	private PlayButton playButton;
	@FXML
	private VolumeButton volumeButton;
	@FXML
	private Replay10Button replay10Button;
	@FXML
	private Forward30Button forward30Button;
	@FXML
	private OpenFileButton openFileButton;
	@FXML
	private Label currentTimeLabel;
	@FXML
	private Label durationLabel;
	@FXML
	private HBox volumeControlsHBox;
	@FXML
	private Label messageLabel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		mediaContainer = new MediaContainer(mediaView, imageView, messageLabel);

		volumeSlider.valueProperty().addListener(observable -> {
			if (mediaPlayer != null) {
				double newVolume = volumeSlider.getValue();
				mediaPlayer.setVolume(newVolume);

				if (newVolume != 0.0) {
					volumeButton.setImage(VolumeButton.VOLUME_BUTTON_IMAGE);
					previousVolume = newVolume;
					isMuted = false;
				} else {
					volumeButton.setImage(VolumeButton.MUTE_BUTTON_IMAGE);
					isMuted = true;
				}
			}
		});

		volumeSlider.setValue(previousVolume);

		setDisableControls(true);

		volumeSlider.setVisible(false);
		volumeSlider.setManaged(false);
	}

	@FXML
	public void handleOpenFileButtonClick(ActionEvent event) {
		File file = fileSelector.showOpenDialog();

		if (file != null) {
			releaseCurrentMediaPlayer();

			try {
				Media media = new Media(file.toURI().toString());
				mediaPlayer = new MediaPlayer(media);
				mediaContainer.setMediaPlayer(mediaPlayer);

				mediaPlayer.setOnReady(() -> {
					progressSlider.setMax(media.getDuration().toSeconds());

					// Check if the file is audio or video
					if (file.getName().endsWith(".mp3") || file.getName().endsWith(".m4a")) {
						mediaContainer.setAlbumCover(extractAlbumCover(file));
					} else {
						mediaContainer.setVisible(MediaContainerElement.MEDIA_VIEW);
					}
				});

				mediaPlayer.setOnEndOfMedia(() -> {
					playButton.setImage(PlayButton.RESTART_BUTTON_IMAGE);
					isEndOfMedia = true;
				});

				currentTimeListener = (observable, oldValue, newValue) -> {
					currentTimeLabel.setText(formatTime(mediaPlayer.getCurrentTime()) + CURRENT_TOTAL_TIME_DELIMITER);

					progressSlider.setValue(newValue.toSeconds());

					// changes restart icon to play icon if the user selected another playback time
					// after the media has ended
					if (newValue.toSeconds() < media.getDuration().toSeconds() && isEndOfMedia) {
						playButton.setImage(PlayButton.PLAY_BUTTON_IMAGE);
						isEndOfMedia = false;
					}
				};

				mediaPlayer.currentTimeProperty().addListener(currentTimeListener);

				mediaPlayer.totalDurationProperty().addListener((observable, oldValue, newValue) -> {
					durationLabel.setText(formatTime(mediaPlayer.getTotalDuration()));
				});

				Player.setAppTitle(String.format(APP_TITLE_FORMAT, Player.APP_NAME, file.getName()));
				playButton.setImage(PlayButton.PAUSE_BUTTON_IMAGE);
				setDisableControls(false);

				if (isMuted) {
					mediaPlayer.setVolume(0);
				} else {
					mediaPlayer.setVolume(previousVolume);
				}

				mediaPlayer.play();

			} catch (MediaException e) {
				handleMediaException("Unsupported media format.");
			}
		}
	}

	@FXML
	public void handlePlayButtonClick(ActionEvent event) {
		handlePlayButtonClick();
	}

	@FXML
	public void handleForward30ButtonClick(ActionEvent event) {
		mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(FORWARD30_BUTTON_PLAYBACK_ADJUSTMENT)));
	}

	@FXML
	public void handleReplay10ButtonClick(ActionEvent event) {
		mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(REPLAY10_BUTTON_PLAYBACK_ADJUSTMENT)));
	}

	@FXML
	public void handleVolumeButtonClick(ActionEvent event) {
		if (isMuted) {
			volumeButton.setImage(VolumeButton.VOLUME_BUTTON_IMAGE);
			volumeSlider.setValue(previousVolume);
			isMuted = false;
		} else {
			volumeButton.setImage(VolumeButton.MUTE_BUTTON_IMAGE);
			volumeSlider.setValue(0);
			isMuted = true;
		}
	}

	@FXML
	public void handleMouseEnteringVolumeButton(MouseEvent event) {
		if (!volumeSlider.isVisible()) {
			volumeSlider.setVisible(true);
			volumeSlider.setManaged(true);
		}
	}

	@FXML
	public void handleMouseExitingVolumeControlsHBox(MouseEvent event) {
		volumeSlider.setVisible(false);
		volumeSlider.setManaged(false);
	}

	@FXML
	public void handleProgressSliderOnMousePressedAndDragged(MouseEvent event) {
		mediaPlayer.seek(Duration.seconds(progressSlider.getValue()));

		// to prevent the media from automatically playing if it has already ended
		if (isEndOfMedia) {
			mediaPlayer.pause();
		}
	}

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
			case LEFT -> adjustPlaybackBySeconds(LEFT_BUTTON_PLAYBACK_ADJUSTMENT);
			case RIGHT -> adjustPlaybackBySeconds(RIGHT_BUTTON_PLAYBACK_ADJUSTMENT);
			case SPACE -> handlePlayButtonClick();
			default -> {
			}
			}
		});
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
		if (mediaPlayer != null) {
			if (isEndOfMedia) {
				isEndOfMedia = false;
				mediaPlayer.seek(Duration.ZERO);
				mediaPlayer.play();
				playButton.setImage(PlayButton.PAUSE_BUTTON_IMAGE);
			} else if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
				mediaPlayer.pause();
				playButton.setImage(PlayButton.PLAY_BUTTON_IMAGE);
			} else {
				mediaPlayer.play();
				playButton.setImage(PlayButton.PAUSE_BUTTON_IMAGE);
			}
		}
	}

	private void releaseCurrentMediaPlayer() {
		try {
			if (mediaPlayer != null) {

				// to avoid errors when mediaPlayer is already disposed off but
				// currentTimeListener is still working
				mediaPlayer.currentTimeProperty().removeListener(currentTimeListener);

				mediaPlayer.stop();
				mediaPlayer.dispose();
				mediaPlayer = null;
			}
		} catch (NullPointerException e) {
			// Underlying 'jfxPlayer' was null because previously opened media file was
			// unsupported.
		}
	}

	private void handleMediaException(String errorMessage) {
		resetControlsBar();
		mediaContainer.displayMessage(errorMessage);
	}

	private void resetControlsBar() {
		playButton.setImage(PlayButton.PLAY_BUTTON_IMAGE);
		progressSlider.setValue(0);
		currentTimeLabel.setText(Player.EMPTY_STRING);
		durationLabel.setText(Player.EMPTY_STRING);
		setDisableControls(true);
	}

	private void setDisableControls(boolean value) {
		playButton.setDisable(value);
		replay10Button.setDisable(value);
		forward30Button.setDisable(value);
		volumeButton.setDisable(value);
		progressSlider.setDisable(value);
	}
}
