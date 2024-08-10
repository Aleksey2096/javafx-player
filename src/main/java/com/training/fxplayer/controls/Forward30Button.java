package com.training.fxplayer.controls;

import static com.training.fxplayer.services.ImageService.loadImage;

import javafx.scene.image.Image;

public class Forward30Button extends FXPlayerButton {

	private static final Image FORWARD_30_BUTTON_IMAGE = loadImage("/com/training/fxplayer/img/forward30.png");

	public Forward30Button() {
		super(FORWARD_30_BUTTON_IMAGE);
	}
}
