package com.training.fxplayer.gui.buttons;

import static com.training.fxplayer.services.ImageService.loadResourceImage;

import javafx.scene.image.Image;

public class Replay10Button extends FXPlayerButton {

	private static final Image REPLAY_10_BUTTON_IMAGE = loadResourceImage("/com/training/fxplayer/img/replay10.png");

	public Replay10Button() {
		super(REPLAY_10_BUTTON_IMAGE);
	}
}
