package com.training.fxplayer;

import java.io.File;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class PlayerController {

	private String path;
	private MediaPlayer mediaPlayer;

	@FXML
	private MediaView mediaView;
	@FXML
	private Slider progressSlider;
	@FXML
	private Slider volumeSlider;

	@FXML
	public void chooseFile(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(null);
		path = file.toURI().toString();

		if (path != null) {
			Media media = new Media(path);
			mediaPlayer = new MediaPlayer(media);
			mediaView.setMediaPlayer(mediaPlayer);

			DoubleProperty widthProperty = mediaView.fitWidthProperty();
			DoubleProperty hightProperty = mediaView.fitHeightProperty();

			widthProperty.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
			hightProperty.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

			mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {

				@Override
				public void changed(ObservableValue<? extends Duration> observable, Duration oldValue,
						Duration newValue) {
					progressSlider.setValue(newValue.toSeconds());
				}
			});

			EventHandler<MouseEvent> changedProgressBar = new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					mediaPlayer.seek(Duration.seconds(progressSlider.getValue()));
				}
			};

			progressSlider.setOnMousePressed(changedProgressBar);

			progressSlider.setOnMouseDragged(changedProgressBar);

			mediaPlayer.setOnReady(new Runnable() {

				@Override
				public void run() {
					Duration total = media.getDuration();
					progressSlider.setMax(total.toSeconds());
				}
			});

			volumeSlider.setValue(mediaPlayer.getVolume() * 100);
			volumeSlider.valueProperty().addListener(new InvalidationListener() {

				@Override
				public void invalidated(Observable observable) {
					mediaPlayer.setVolume(volumeSlider.getValue() / 100);
				}
			});

			mediaPlayer.play();
		}
	}

	@FXML
	public void play(ActionEvent event) {
		mediaPlayer.play();
		mediaPlayer.setRate(1);
	}

	@FXML
	public void pause(ActionEvent event) {
		mediaPlayer.pause();
	}

	@FXML
	public void stop(ActionEvent event) {
		mediaPlayer.stop();
	}

	@FXML
	public void slowRate(ActionEvent event) {
		mediaPlayer.setRate(0.5);
	}

	@FXML
	public void fastForward(ActionEvent event) {
		mediaPlayer.setRate(2);
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
