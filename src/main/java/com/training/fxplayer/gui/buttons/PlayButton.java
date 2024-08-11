package com.training.fxplayer.gui.buttons;

import static com.training.fxplayer.services.ImageService.loadResourceImage;

import javafx.scene.image.Image;

public class PlayButton extends FXPlayerButton {
	public static final Image PLAY_BUTTON_IMAGE = loadResourceImage("/com/training/fxplayer/img/play.png");
	public static final Image PAUSE_BUTTON_IMAGE = loadResourceImage("/com/training/fxplayer/img/pause.png");
	public static final Image RESTART_BUTTON_IMAGE = loadResourceImage("/com/training/fxplayer/img/restart.png");

	public PlayButton() {
		super(PLAY_BUTTON_IMAGE);
	}
}
