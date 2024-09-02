package com.training.fxplayer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class PlayerController implements Initializable {

	private static final String INITIAL_DIRECTORY = "D:/My documents/Downloads";
	private static final String EXTENSION_FILTER_DESCRIPTION = "Video and Music Files";
	private static final String AUXILIARY_EXTENSION_FILTER_DESCRIPTION = "All Files";
	private static final String[] SUPPORTED_EXTENSIONS = { "*.mp4", "*.flv", "*.mp3", "*.m4a" };
	private static final String ALL_EXTENSIONS = "*.*";
	private static final String FULL_TIME_FORMAT = "%d:%02d:%02d";
	private static final String SHORT_TIME_FORMAT = "%d:%02d";
	private static final int NUMBER_OF_MINUTES_IN_HOUR = 60;
	private static final int DEFAULT_ICON_SIZE = 25;
	private static final double INITIAL_VOLUME = 0.7;
	private static final int LEFT_BUTTON_PLAYBACK_ADJUSTMENT = -60;
	private static final int RIGHT_BUTTON_PLAYBACK_ADJUSTMENT = 60;
	private static final int REPLAY10_BUTTON_PLAYBACK_ADJUSTMENT = -10;
	private static final int FORWARD30_BUTTON_PLAYBACK_ADJUSTMENT = 30;
	private static final String APP_TITLE_FORMAT = "%s - %s";
	private static final String CURRENT_TOTAL_TIME_DELIMITER = " / ";
	private static final String WIDTH_PROPERTY = "width";
	private static final String HEIGHT_PROPERTY = "height";
	private static final String COLON = ":";

	// Delay to distinguish between single and double click
	private static final PauseTransition PAUSE_TRANSITION = new PauseTransition(Duration.millis(200));
	private static final FileChooser FILE_CHOOSER = new FileChooser();
	private final PlaybackPositionStorage playbackPositionStorage = new PlaybackPositionStorage();

	private MediaPlayer mediaPlayer;
	private boolean isEndOfMedia;
	private double previousVolume;
	private boolean isMuted;

	private ChangeListener<Duration> currentTimeListener;
	Image defaultAlbumCoverImage;

	@FXML
	private VBox controlsBox;
	@FXML
	private StackPane stackPane;
	@FXML
	private MediaView mediaView;
	@FXML
	private ImageView imageView;
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
	@FXML
	private Label messageLabel;

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

		configureFileChooser();

		fitViewsIntoScene();

		bindControlsImages();

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

		/*
		 * SET THE DEFAULTS
		 */

		defaultAlbumCoverImage = new Image(
				getClass().getResourceAsStream("/com/training/fxplayer/img/default_album_cover.jpg"));

		previousVolume = INITIAL_VOLUME;
		isMuted = false;
		volumeSlider.setValue(previousVolume);

		setDisableControls(true);

		playButton.setGraphic(ivPlay);
		isEndOfMedia = false;

		replay10Button.setGraphic(ivReplay10);

		forward30Button.setGraphic(ivForward30);

		volumeButton.setGraphic(ivVolume);

		openFileButton.setGraphic(ivOpenFile);

		volumeSlider.setVisible(false);
		volumeSlider.setManaged(false);

		messageLabel.setWrapText(true);
	}

	@FXML
	public void handleOpenFileButtonClick(ActionEvent event) {
		File file = FILE_CHOOSER.showOpenDialog(Player.getPrimaryStage());

		openFile(file);
	}

	public void openFile(File file) {
		if (file != null) {
			handleCurrentMediaPlayer();

			try {
				Media media = new Media(file.toURI().toString());
				mediaPlayer = new MediaPlayer(media);
				mediaView.setMediaPlayer(mediaPlayer);

				mediaPlayer.setOnReady(() -> {
					progressSlider.setMax(media.getDuration().toSeconds());

					// Check if the file is audio or video
					if (file.getName().endsWith(".mp3") || file.getName().endsWith(".m4a")) {
						Image albumCover = extractAlbumCover(file);

						imageView.setVisible(true);
						mediaView.setVisible(false);
						messageLabel.setVisible(false);
						if (albumCover != null) {
							imageView.setImage(albumCover);
						} else {
							imageView.setImage(defaultAlbumCoverImage);
						}
					} else {
						// If it's a video file
						imageView.setVisible(false);
						mediaView.setVisible(true);
						messageLabel.setVisible(false);
					}

					// Load the last known position
					Double lastPosition = playbackPositionStorage.getPosition(media.getSource());
					if (lastPosition != null) {
						// Rewind by 5 seconds, but not before the start of the media
						double rewindPosition = Math.max(0, lastPosition - 5);
						mediaPlayer.seek(Duration.seconds(rewindPosition));
					}
				});

				mediaPlayer.setOnEndOfMedia(() -> {
					playButton.setGraphic(ivRestart);
					isEndOfMedia = true;
				});

				mediaPlayer.setOnError(() -> {
					String errorMessage = mediaPlayer.getError().getMessage();
					// removes unwanted prefix
					int index = errorMessage.lastIndexOf(COLON);
					if (index != -1) {
						errorMessage = errorMessage.substring(index + 1).trim();
					}
					releaseCurrentMediaPlayer();
					handleMediaException(errorMessage);
				});

				currentTimeListener = (observable, oldValue, newValue) -> {
					currentTimeLabel.setText(formatTime(mediaPlayer.getCurrentTime()) + CURRENT_TOTAL_TIME_DELIMITER);

					progressSlider.setValue(newValue.toSeconds());

					// changes restart icon to play icon if the user selected another playback time
					// after the media has ended
					if (newValue.toSeconds() < media.getDuration().toSeconds() && isEndOfMedia) {
						playButton.setGraphic(ivPlay);
						isEndOfMedia = false;
					}
				};

				mediaPlayer.currentTimeProperty().addListener(currentTimeListener);

				mediaPlayer.totalDurationProperty().addListener((observable, oldValue, newValue) -> {
					durationLabel.setText(formatTime(mediaPlayer.getTotalDuration()));
				});

				Player.setAppTitle(String.format(APP_TITLE_FORMAT, Player.APP_NAME, file.getName()));
				playButton.setGraphic(ivPause);
				setDisableControls(false);

				if (isMuted) {
					mediaPlayer.setVolume(0);
				} else {
					mediaPlayer.setVolume(previousVolume);
				}

				mediaPlayer.play();

				// Handle window close event
				Player.getPrimaryStage().setOnCloseRequest(event -> {
					if (mediaPlayer != null) {
						playbackPositionStorage.savePosition(media.getSource(),
								mediaPlayer.getCurrentTime().toSeconds());
					}
					playbackPositionStorage.saveToFile();
				});

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
			volumeButton.setGraphic(ivVolume);
			volumeSlider.setValue(previousVolume);
			isMuted = false;
		} else {
			volumeButton.setGraphic(ivMute);
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

	private void bindControlsImages() {

		// Get the paths of the images and make them into images.

		// Button play image
		Image imagePlay = new Image(getClass().getResourceAsStream("/com/training/fxplayer/img/play.png"));
		ivPlay = new ImageView(imagePlay);
		ivPlay.setFitWidth(DEFAULT_ICON_SIZE);
		ivPlay.setFitHeight(DEFAULT_ICON_SIZE);

		// Button pause image.
		Image imagePause = new Image(getClass().getResourceAsStream("/com/training/fxplayer/img/pause.png"));
		ivPause = new ImageView(imagePause);
		ivPause.setFitHeight(DEFAULT_ICON_SIZE);
		ivPause.setFitWidth(DEFAULT_ICON_SIZE);

		// Button restart image.
		Image imageRestart = new Image(getClass().getResourceAsStream("/com/training/fxplayer/img/restart.png"));
		ivRestart = new ImageView(imageRestart);
		ivRestart.setFitWidth(DEFAULT_ICON_SIZE);
		ivRestart.setFitHeight(DEFAULT_ICON_SIZE);

		// Button mute image.
		Image imageMute = new Image(getClass().getResourceAsStream("/com/training/fxplayer/img/mute.png"));
		ivMute = new ImageView(imageMute);
		ivMute.setFitWidth(DEFAULT_ICON_SIZE);
		ivMute.setFitHeight(DEFAULT_ICON_SIZE);

		// Button volume image.
		Image imageVolume = new Image(getClass().getResourceAsStream("/com/training/fxplayer/img/volume.png"));
		ivVolume = new ImageView(imageVolume);
		ivVolume.setFitWidth(DEFAULT_ICON_SIZE);
		ivVolume.setFitHeight(DEFAULT_ICON_SIZE);

		// Button forward_30 image.
		Image imageForward30 = new Image(getClass().getResourceAsStream("/com/training/fxplayer/img/forward30.png"));
		ivForward30 = new ImageView(imageForward30);
		ivForward30.setFitWidth(DEFAULT_ICON_SIZE);
		ivForward30.setFitHeight(DEFAULT_ICON_SIZE);

		// Button replay_10 image.
		Image imageReplay10 = new Image(getClass().getResourceAsStream("/com/training/fxplayer/img/replay10.png"));
		ivReplay10 = new ImageView(imageReplay10);
		ivReplay10.setFitWidth(DEFAULT_ICON_SIZE);
		ivReplay10.setFitHeight(DEFAULT_ICON_SIZE);

		// Button open_file image.
		Image imageOpenFile = new Image(getClass().getResourceAsStream("/com/training/fxplayer/img/openFile.png"));
		ivOpenFile = new ImageView(imageOpenFile);
		ivOpenFile.setFitWidth(DEFAULT_ICON_SIZE);
		ivOpenFile.setFitHeight(DEFAULT_ICON_SIZE);
	}

	private String formatTime(Duration duration) {
		int hours = (int) duration.toHours();
		int minutes = (int) duration.toMinutes() % NUMBER_OF_MINUTES_IN_HOUR;
		int seconds = (int) duration.toSeconds() % NUMBER_OF_MINUTES_IN_HOUR;

		if (hours > 0) {
			return String.format(FULL_TIME_FORMAT, hours, minutes, seconds);
		} else {
			return String.format(SHORT_TIME_FORMAT, minutes, seconds);
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
		if (mediaPlayer != null) {
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

	private void handleCurrentMediaPlayer() {
		if (mediaPlayer != null) {
			playbackPositionStorage.savePosition(mediaPlayer.getMedia().getSource(),
					mediaPlayer.getCurrentTime().toSeconds());
			releaseCurrentMediaPlayer();
		}
	}

	private void releaseCurrentMediaPlayer() {
		try {
			// to avoid errors when mediaPlayer is already disposed off but
			// currentTimeListener is still working
			mediaPlayer.currentTimeProperty().removeListener(currentTimeListener);

			mediaPlayer.stop();
			mediaPlayer.dispose();
			mediaPlayer = null;
		} catch (NullPointerException e) {
			// Underlying 'jfxPlayer' was null because previously opened media file was
			// unsupported.
		}
	}

	private void configureFileChooser() {
		FILE_CHOOSER.setInitialDirectory(new File(INITIAL_DIRECTORY));
		FILE_CHOOSER.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter(EXTENSION_FILTER_DESCRIPTION, SUPPORTED_EXTENSIONS),
				new FileChooser.ExtensionFilter(AUXILIARY_EXTENSION_FILTER_DESCRIPTION, ALL_EXTENSIONS));
	}

	private void fitViewsIntoScene() {
		DoubleProperty mediaWidthProperty = mediaView.fitWidthProperty();
		DoubleProperty mediaHightProperty = mediaView.fitHeightProperty();

		mediaWidthProperty.bind(Bindings.selectDouble(mediaView.sceneProperty(), WIDTH_PROPERTY));
		mediaHightProperty.bind(Bindings.selectDouble(mediaView.sceneProperty(), HEIGHT_PROPERTY));

		mediaView.setPreserveRatio(true);

		DoubleProperty imageWidthProperty = imageView.fitWidthProperty();
		DoubleProperty imageHightProperty = imageView.fitHeightProperty();

		imageWidthProperty.bind(Bindings.selectDouble(imageView.sceneProperty(), WIDTH_PROPERTY));
		imageHightProperty.bind(Bindings.selectDouble(imageView.sceneProperty(), HEIGHT_PROPERTY));

		imageView.setPreserveRatio(true);
	}

	private void displayMessage(String message) {
		messageLabel.setText(message);
		messageLabel.setVisible(true);
		mediaView.setVisible(false);
		imageView.setVisible(false);
	}

	private void handleMediaException(String errorMessage) {
		Player.setAppTitle(Player.APP_NAME);
		resetControlsBar();
		displayMessage(errorMessage);
	}

	private void resetControlsBar() {
		playButton.setGraphic(ivPlay);
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

	private Image extractAlbumCover(File file) {
		try {
			Tag tag = AudioFileIO.read(file).getTag();
			if (tag != null) {
				Artwork artwork = tag.getFirstArtwork();
				if (artwork != null) {
					byte[] imageData = artwork.getBinaryData();
					return new Image(new ByteArrayInputStream(imageData));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
