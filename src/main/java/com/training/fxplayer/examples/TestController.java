package com.training.fxplayer.examples;

import java.io.File;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;
import uk.co.caprica.vlcj.media.MediaRef;
import uk.co.caprica.vlcj.media.TrackType;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class TestController {

	private final EmbeddedMediaPlayer embeddedMediaPlayer;

	private String path;

	@FXML
	private ImageView imageView;
	@FXML
	private Slider progressSlider;
	@FXML
	private Slider volumeSlider;

	public TestController() {
		embeddedMediaPlayer = Test.getEmbeddedMediaPlayer();
		embeddedMediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventListener() {

			@Override
			public void volumeChanged(MediaPlayer mediaPlayer, float volume) {
			}

			@Override
			public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
			}

			@Override
			public void titleChanged(MediaPlayer mediaPlayer, int newTitle) {
			}

			@Override
			public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
				progressSlider.setValue(newTime);
			}

			@Override
			public void stopped(MediaPlayer mediaPlayer) {
			}

			@Override
			public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {
			}

			@Override
			public void seekableChanged(MediaPlayer mediaPlayer, int newSeekable) {
			}

			@Override
			public void scrambledChanged(MediaPlayer mediaPlayer, int newScrambled) {
			}

			@Override
			public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
			}

			@Override
			public void playing(MediaPlayer mediaPlayer) {
			}

			@Override
			public void paused(MediaPlayer mediaPlayer) {
			}

			@Override
			public void pausableChanged(MediaPlayer mediaPlayer, int newPausable) {
			}

			@Override
			public void opening(MediaPlayer mediaPlayer) {
			}

			@Override
			public void muted(MediaPlayer mediaPlayer, boolean muted) {
			}

			@Override
			public void mediaPlayerReady(MediaPlayer mediaPlayer) {
				long totalDuration = mediaPlayer.media().info().duration();
				progressSlider.setMax(totalDuration);
			}

			@Override
			public void mediaChanged(MediaPlayer mediaPlayer, MediaRef media) {
			}

			@Override
			public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
			}

			@Override
			public void forward(MediaPlayer mediaPlayer) {
			}

			@Override
			public void finished(MediaPlayer mediaPlayer) {
			}

			@Override
			public void error(MediaPlayer mediaPlayer) {
			}

			@Override
			public void elementaryStreamSelected(MediaPlayer mediaPlayer, TrackType type, int id) {
			}

			@Override
			public void elementaryStreamDeleted(MediaPlayer mediaPlayer, TrackType type, int id) {
			}

			@Override
			public void elementaryStreamAdded(MediaPlayer mediaPlayer, TrackType type, int id) {
			}

			@Override
			public void corked(MediaPlayer mediaPlayer, boolean corked) {
			}

			@Override
			public void chapterChanged(MediaPlayer mediaPlayer, int newChapter) {
			}

			@Override
			public void buffering(MediaPlayer mediaPlayer, float newCache) {
			}

			@Override
			public void backward(MediaPlayer mediaPlayer) {
			}

			@Override
			public void audioDeviceChanged(MediaPlayer mediaPlayer, String audioDevice) {
			}
		});
	}

	@FXML
	public void chooseFile(ActionEvent event) {

		embeddedMediaPlayer.videoSurface().set(new ImageViewVideoSurface(imageView));
		imageView.setPreserveRatio(true);

		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(null);
		path = "file:///" + file.getAbsolutePath();

		if (path != null) {

			DoubleProperty widthProperty = imageView.fitWidthProperty();
			DoubleProperty hightProperty = imageView.fitHeightProperty();

			widthProperty.bind(Bindings.selectDouble(imageView.sceneProperty(), "width"));
			hightProperty.bind(Bindings.selectDouble(imageView.sceneProperty(), "height"));

			EventHandler<MouseEvent> changedProgressBar = new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					embeddedMediaPlayer.controls()
							.setTime((long) Duration.seconds(progressSlider.getValue()).toSeconds());
				}
			};

			progressSlider.setOnMousePressed(changedProgressBar);

			progressSlider.setOnMouseDragged(changedProgressBar);

			volumeSlider.setMax(200);
			volumeSlider.setValue(100);
			volumeSlider.valueProperty().addListener(new InvalidationListener() {

				@Override
				public void invalidated(Observable observable) {
					embeddedMediaPlayer.audio().setVolume((int) volumeSlider.getValue());
				}
			});

			embeddedMediaPlayer.media().play(path);
		}
	}

	@FXML
	public void play(ActionEvent event) {
		embeddedMediaPlayer.controls().play();
		embeddedMediaPlayer.controls().setRate(1);
	}

	@FXML
	public void pause(ActionEvent event) {
		embeddedMediaPlayer.controls().pause();
	}

	@FXML
	public void stop(ActionEvent event) {
		embeddedMediaPlayer.controls().stop();
	}

	@FXML
	public void slowRate(ActionEvent event) {
		embeddedMediaPlayer.controls().setRate(0.5f);
	}

	@FXML
	public void fastForward(ActionEvent event) {
		embeddedMediaPlayer.controls().setRate(2);
	}

	@FXML
	public void skip30(ActionEvent event) {
		embeddedMediaPlayer.controls().skipTime(30);
	}

	@FXML
	public void back10(ActionEvent event) {
		embeddedMediaPlayer.controls().skipTime(-10);
	}
}