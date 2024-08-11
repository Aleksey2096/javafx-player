package com.training.fxplayer.gui.buttons;

import static com.training.fxplayer.services.ImageService.loadResourceImage;

import javafx.scene.image.Image;

public class OpenFileButton extends FXPlayerButton {

	private static final Image OPEN_FILE_BUTTON_IMAGE = loadResourceImage("/com/training/fxplayer/img/openFile.png");

	public OpenFileButton() {
		super(OPEN_FILE_BUTTON_IMAGE);
	}
}
