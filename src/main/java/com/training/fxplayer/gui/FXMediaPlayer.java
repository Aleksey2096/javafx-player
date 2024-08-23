package com.training.fxplayer.gui;

import static com.training.fxplayer.services.ImageService.extractAlbumCover;
import static com.training.fxplayer.services.TimeFormatterService.formatTime;

import java.io.File;

import com.training.fxplayer.domain.MediaState;
import com.training.fxplayer.gui.MediaContainer.MediaContainerElement;
import com.training.fxplayer.gui.buttons.PlayButton;
import com.training.fxplayer.services.PlaybackPositionStorage;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class FXMediaPlayer {

	private static final String CURRENT_TOTAL_TIME_DELIMITER = " / ";
	private static final String MP3_EXTENSION = ".mp3";
	private static final String M4A_EXTENSION = ".m4a";
	private static final int REWIND_TIME = 5;

	private MediaPlayer mediaPlayer;
	private Media media;
	private MediaState mediaState;
	private PlaybackPositionStorage playbackPositionStorage;
	private ChangeListener<Duration> currentTimeListener;

	public FXMediaPlayer(Media media, MediaState mediaState, PlaybackPositionStorage playbackPositionStorage) {
		this.media = media;
		this.mediaState = mediaState;
		this.playbackPositionStorage = playbackPositionStorage;
		mediaPlayer = new MediaPlayer(media);
	}

	public MediaPlayer retrieveMediaPlayer() {
		return mediaPlayer;
	}

	public void setVolume(double value) {
		mediaPlayer.setVolume(value);
	}

	public void seek(Duration seekTime) {
		mediaPlayer.seek(seekTime);
	}

	public void pause() {
		mediaPlayer.pause();
	}

	public void play() {
		mediaPlayer.play();
	}

	public Status getStatus() {
		return mediaPlayer.getStatus();
	}

	public void setOnReadyEventHandler(Slider progressSlider, MediaContainer mediaContainer, File file) {
		mediaPlayer.setOnReady(() -> {
			progressSlider.setMax(media.getDuration().toSeconds());

			// Check if the file is audio or video
			if (file.getName().endsWith(MP3_EXTENSION) || file.getName().endsWith(M4A_EXTENSION)) {
				mediaContainer.setAlbumCover(extractAlbumCover(file));
			} else {
				mediaContainer.setVisible(MediaContainerElement.MEDIA_VIEW);
			}

			// Load the last known position
			Double lastPosition = playbackPositionStorage.getPosition(media.getSource());
			if (lastPosition != null) {
				// Rewind by 5 seconds, but not before the start of the media
				double rewindPosition = Math.max(0, lastPosition - REWIND_TIME);
				mediaPlayer.seek(Duration.seconds(rewindPosition));
			}

			setInitialVolume();

			mediaPlayer.play();
		});
	}

	public void setOnEndOfMediaEventHandler(PlayButton playButton) {
		mediaPlayer.setOnEndOfMedia(() -> {
			playButton.setImage(PlayButton.RESTART_BUTTON_IMAGE);
			mediaState.setEndOfMedia(true);
		});
	}

	public void addCurrentTimeListener(Label currentTimeLabel, Slider progressSlider, PlayButton playButton) {
		currentTimeListener = (observable, oldValue, newValue) -> {
			currentTimeLabel.setText(formatTime(mediaPlayer.getCurrentTime()) + CURRENT_TOTAL_TIME_DELIMITER);

			progressSlider.setValue(newValue.toSeconds());

			// changes restart icon to play icon if the user selected another playback time
			// after the media has ended
			if (newValue.toSeconds() < media.getDuration().toSeconds() && mediaState.isEndOfMedia()) {
				playButton.setImage(PlayButton.PLAY_BUTTON_IMAGE);
				mediaState.setEndOfMedia(false);
			}
		};

		mediaPlayer.currentTimeProperty().addListener(currentTimeListener);
	}

	public void addTotalDurationListener(Label durationLabel) {
		mediaPlayer.totalDurationProperty().addListener((observable, oldValue, newValue) -> {
			durationLabel.setText(formatTime(mediaPlayer.getTotalDuration()));
		});
	}

	public EventHandler<WindowEvent> createSetOnCloseEventHandler() {
		return event -> {
			playbackPositionStorage.savePosition(media.getSource(), mediaPlayer.getCurrentTime().toSeconds());
			playbackPositionStorage.saveToFile();
		};
	}

	public void adjustPlaybackBySeconds(int seconds) {
		if (mediaPlayer != null) {
			mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(seconds)));
		}
	}

	public void handleCurrentMediaPlayer() {
		if (mediaPlayer != null) {
			playbackPositionStorage.savePosition(mediaPlayer.getMedia().getSource(),
					mediaPlayer.getCurrentTime().toSeconds());
			releaseCurrentMediaPlayer();
		}
	}

	public boolean isInternalPlayerNull() {
		return mediaPlayer == null;
	}

	private void setInitialVolume() {
		if (mediaState.isMuted()) {
			mediaPlayer.setVolume(0);
		} else {
			mediaPlayer.setVolume(mediaState.getPreviousVolume());
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
}
