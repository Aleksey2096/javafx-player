package com.training.fxplayer.gui;

import static com.training.fxplayer.services.ImageService.loadResourceImage;

import com.training.fxplayer.gui.views.FXPlayerImageView;
import com.training.fxplayer.gui.views.FXPlayerMediaView;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;

public class MediaContainer {
	private static final Image DEFAULT_ALBUM_COVER = loadResourceImage(
			"/com/training/fxplayer/img/default_album_cover.jpg");

	public enum MediaContainerElement {
		MEDIA_VIEW, IMAGE_VIEW, MESSAGE_LABEL
	}

	private final FXPlayerMediaView mediaView;
	private final FXPlayerImageView imageView;
	private final Label messageLabel;

	public MediaContainer(FXPlayerMediaView mediaView, FXPlayerImageView imageView, Label messageLabel) {
		this.mediaView = mediaView;
		this.imageView = imageView;
		this.messageLabel = messageLabel;

		mediaView.fitIntoScene(true);
		imageView.fitIntoScene(true);
		messageLabel.setWrapText(true);
	}

	public void setMediaPlayer(MediaPlayer mediaPlayer) {
		mediaView.setMediaPlayer(mediaPlayer);
	}

	public void setVisible(MediaContainerElement mediaContainerElement) {
		imageView.setVisible(false);
		mediaView.setVisible(false);
		messageLabel.setVisible(false);

		switch (mediaContainerElement) {
		case IMAGE_VIEW -> imageView.setVisible(true);
		case MEDIA_VIEW -> mediaView.setVisible(true);
		case MESSAGE_LABEL -> messageLabel.setVisible(true);
		}
	}

	public void setAlbumCover(Image albumCover) {
		setVisible(MediaContainerElement.IMAGE_VIEW);

		if (albumCover != null) {
			imageView.setImage(albumCover);
		} else {
			imageView.setImage(DEFAULT_ALBUM_COVER);
		}
	}

	public void displayMessage(String message) {
		messageLabel.setText(message);
		setVisible(MediaContainerElement.MESSAGE_LABEL);
	}
}
