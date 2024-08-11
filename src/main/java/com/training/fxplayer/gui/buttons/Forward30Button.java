package com.training.fxplayer.gui.buttons;

import static com.training.fxplayer.services.ImageService.loadResourceImage;

import javafx.scene.image.Image;

public class Forward30Button extends FXPlayerButton {

	private static final Image FORWARD_30_BUTTON_IMAGE = loadResourceImage("/com/training/fxplayer/img/forward30.png");

	public Forward30Button() {
		super(FORWARD_30_BUTTON_IMAGE);
	}
}
