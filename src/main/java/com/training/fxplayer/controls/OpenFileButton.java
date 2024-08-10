package com.training.fxplayer.controls;

import static com.training.fxplayer.services.ImageService.loadImage;

import javafx.scene.image.Image;

public class OpenFileButton extends FXPlayerButton {

	private static final Image OPEN_FILE_BUTTON_IMAGE = loadImage("/com/training/fxplayer/img/openFile.png");

	public OpenFileButton() {
		super(OPEN_FILE_BUTTON_IMAGE);
	}
}
