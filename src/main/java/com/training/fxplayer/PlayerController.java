package com.training.fxplayer;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import com.training.fxplayer.domain.MediaState;
import com.training.fxplayer.gui.FXMediaPlayer;
import com.training.fxplayer.gui.FileSelector;
import com.training.fxplayer.gui.MediaContainer;
import com.training.fxplayer.gui.VolumeSlider;
import com.training.fxplayer.gui.buttons.Forward30Button;
import com.training.fxplayer.gui.buttons.OpenFileButton;
import com.training.fxplayer.gui.buttons.PlayButton;
import com.training.fxplayer.gui.buttons.Replay10Button;
import com.training.fxplayer.gui.buttons.VolumeButton;
import com.training.fxplayer.gui.views.FXPlayerImageView;
import com.training.fxplayer.gui.views.FXPlayerMediaView;
import com.training.fxplayer.services.PlaybackPositionStorage;

import javafx.animation.PauseTransition;
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

	private static final int LEFT_BUTTON_PLAYBACK_ADJUSTMENT = -60;
	private static final int RIGHT_BUTTON_PLAYBACK_ADJUSTMENT = 60;
	private static final int REPLAY10_BUTTON_PLAYBACK_ADJUSTMENT = -10;
	private static final int FORWARD30_BUTTON_PLAYBACK_ADJUSTMENT = 30;
	private static final String APP_TITLE_FORMAT = "%s - %s";
	private static final String MEDIA_EXCEPTION_MESSAGE = "Unsupported media format.";

	// Delay to distinguish between single and double click
	private static final PauseTransition PAUSE_TRANSITION = new PauseTransition(Duration.millis(200));

	private final FileSelector fileSelector = new FileSelector(Player.getPrimaryStage());
	private final PlaybackPositionStorage playbackPositionStorage = new PlaybackPositionStorage();
	private final MediaState mediaState = new MediaState();

	private FXMediaPlayer fxMediaPlayer;
	private MediaContainer mediaContainer;

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
	private VolumeSlider volumeSlider;
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

		volumeSlider.setValue(mediaState.getPreviousVolume());
		volumeSlider.valueProperty().addListener(observable -> {
			double newVolume = volumeSlider.getValue();
			fxMediaPlayer.setVolume(newVolume);

			if (newVolume != 0.0) {
				volumeButton.setImage(VolumeButton.VOLUME_BUTTON_IMAGE);
				mediaState.setPreviousVolume(newVolume);
				mediaState.setMuted(false);
			} else {
				volumeButton.setImage(VolumeButton.MUTE_BUTTON_IMAGE);
				mediaState.setMuted(true);
			}
		});

		setDisableControls(true);
	}

	@FXML
	public void handleOpenFileButtonClick(ActionEvent event) {
		File file = fileSelector.showOpenDialog();

		openFile(file);
	}

	public void openFile(File file) {
		if (file != null) {
			if (fxMediaPlayer != null) {
				fxMediaPlayer.handleCurrentMediaPlayer();
			}

			try {
				Media media = new Media(file.toURI().toString());
				fxMediaPlayer = new FXMediaPlayer(media, mediaState, playbackPositionStorage);
				mediaContainer.setMediaPlayer(fxMediaPlayer.retrieveMediaPlayer());

				fxMediaPlayer.setOnReadyEventHandler(progressSlider, mediaContainer, file);
				fxMediaPlayer.setOnEndOfMediaEventHandler(playButton);
				fxMediaPlayer.setOnErrorEventHandler(this);
				fxMediaPlayer.addCurrentTimeListener(currentTimeLabel, progressSlider, playButton);
				fxMediaPlayer.addTotalDurationListener(durationLabel);

				Player.setAppTitle(String.format(APP_TITLE_FORMAT, Player.APP_NAME, file.getName()));
				playButton.setImage(PlayButton.PAUSE_BUTTON_IMAGE);
				setDisableControls(false);

				// Handle window close event
				Player.getPrimaryStage().setOnCloseRequest(fxMediaPlayer.createSetOnCloseEventHandler());

			} catch (MediaException e) {
				handleMediaException(MEDIA_EXCEPTION_MESSAGE);
			}
		}
	}

	@FXML
	public void handlePlayButtonClick(ActionEvent event) {
		handlePlayButtonClick();
	}

	@FXML
	public void handleForward30ButtonClick(ActionEvent event) {
		fxMediaPlayer.adjustPlaybackBySeconds(FORWARD30_BUTTON_PLAYBACK_ADJUSTMENT);
	}

	@FXML
	public void handleReplay10ButtonClick(ActionEvent event) {
		fxMediaPlayer.adjustPlaybackBySeconds(REPLAY10_BUTTON_PLAYBACK_ADJUSTMENT);
	}

	@FXML
	public void handleVolumeButtonClick(ActionEvent event) {
		if (mediaState.isMuted()) {
			volumeButton.setImage(VolumeButton.VOLUME_BUTTON_IMAGE);
			volumeSlider.setValue(mediaState.getPreviousVolume());
			mediaState.setMuted(false);
		} else {
			volumeButton.setImage(VolumeButton.MUTE_BUTTON_IMAGE);
			volumeSlider.setValue(0);
			mediaState.setMuted(true);
		}
	}

	@FXML
	public void handleMouseEnteringVolumeButton(MouseEvent event) {
		if (!volumeSlider.isVisible()) {
			volumeSlider.setVisibility(true);
		}
	}

	@FXML
	public void handleMouseExitingVolumeControlsHBox(MouseEvent event) {
		volumeSlider.setVisibility(false);
	}

	@FXML
	public void handleProgressSliderOnMousePressedAndDragged(MouseEvent event) {
		fxMediaPlayer.seek(Duration.seconds(progressSlider.getValue()));

		// to prevent the media from automatically playing if it has already ended
		if (mediaState.isEndOfMedia()) {
			fxMediaPlayer.pause();
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
			case LEFT -> {
				if (fxMediaPlayer != null) {
					fxMediaPlayer.adjustPlaybackBySeconds(LEFT_BUTTON_PLAYBACK_ADJUSTMENT);
				}
			}
			case RIGHT -> {
				if (fxMediaPlayer != null) {
					fxMediaPlayer.adjustPlaybackBySeconds(RIGHT_BUTTON_PLAYBACK_ADJUSTMENT);
				}
			}
			case SPACE -> {
				/*
				 * Prevents the default behavior of the Space key that is commonly used in
				 * Windows to activate the currently focused button or control
				 */
				keyEvent.consume();

				handlePlayButtonClick();
			}
			default -> {
				// Ignore unused key events
			}
			}
		});
	}

	public void handleMediaException(String errorMessage) {
		Player.setAppTitle(Player.APP_NAME);
		resetControlsBar();
		mediaContainer.displayMessage(errorMessage);
	}

	private void toggleControlsBarVisibility() {
		controlsBox.setVisible(!controlsBox.isVisible());
		controlsBox.setManaged(!controlsBox.isManaged());
	}

	private void setControlsBarVisibility(boolean isVisible) {
		controlsBox.setVisible(isVisible);
		controlsBox.setManaged(isVisible);
	}

	private void handlePlayButtonClick() {
		if (fxMediaPlayer != null && !fxMediaPlayer.isInternalPlayerNull()) {
			if (mediaState.isEndOfMedia()) {
				mediaState.setEndOfMedia(false);
				fxMediaPlayer.seek(Duration.ZERO);
				fxMediaPlayer.play();
				playButton.setImage(PlayButton.PAUSE_BUTTON_IMAGE);
			} else if (fxMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
				fxMediaPlayer.pause();
				playButton.setImage(PlayButton.PLAY_BUTTON_IMAGE);
			} else {
				fxMediaPlayer.play();
				playButton.setImage(PlayButton.PAUSE_BUTTON_IMAGE);
			}
		}
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
