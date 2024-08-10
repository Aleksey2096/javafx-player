package com.training.fxplayer.controls;

import static com.training.fxplayer.services.ImageService.loadImage;

import javafx.scene.image.Image;

public class Replay10Button extends FXPlayerButton {

	private static final Image REPLAY_10_BUTTON_IMAGE = loadImage("/com/training/fxplayer/img/replay10.png");

	public Replay10Button() {
		super(REPLAY_10_BUTTON_IMAGE);
	}
}
