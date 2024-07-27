package com.training.fxplayer;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class PlayerController implements Initializable {

	private static final FileChooser FILE_CHOOSER = new FileChooser();

	private MediaPlayer mediaPlayer;
	private boolean isEndOfMedia;

	@FXML
	private MediaView mediaView;
	@FXML
	private Slider progressSlider;
	@FXML
	private Slider volumeSlider;
	@FXML
	private Button playButton;

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

		EventHandler<MouseEvent> changedProgressBar = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mediaPlayer.seek(Duration.seconds(progressSlider.getValue()));
			}
		};

		progressSlider.setOnMousePressed(changedProgressBar);
		progressSlider.setOnMouseDragged(changedProgressBar);

		volumeSlider.setValue(70);
		volumeSlider.valueProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				mediaPlayer.setVolume(volumeSlider.getValue() / 100);
			}
		});

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

		/*
		 * SET THE DEFAULTS
		 */

		playButton.setGraphic(ivPlay);
		playButton.setDisable(true);
		isEndOfMedia = false;
	}

	@FXML
	public void chooseFile(ActionEvent event) {
		File file = FILE_CHOOSER.showOpenDialog(null);

		if (file != null) {
			Media media = new Media(file.toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			mediaView.setMediaPlayer(mediaPlayer);

			mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
				@Override
				public void changed(ObservableValue<? extends Duration> observable, Duration oldValue,
						Duration newValue) {
					progressSlider.setValue(newValue.toSeconds());
				}
			});

			mediaPlayer.setOnReady(new Runnable() {
				@Override
				public void run() {
					progressSlider.setMax(media.getDuration().toSeconds());
				}
			});

			mediaPlayer.setOnEndOfMedia(new Runnable() {
				@Override
				public void run() {
					playButton.setGraphic(ivRestart);
					isEndOfMedia = true;
				}
			});

			Player.setAppTitle(String.format("%s - %s", Player.APP_NAME, file.getName()));
			mediaPlayer.play();
			playButton.setGraphic(ivPause);
			playButton.setDisable(false);
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
}
