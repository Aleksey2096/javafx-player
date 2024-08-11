package com.training.fxplayer.gui.buttons;

import static com.training.fxplayer.services.ImageService.loadResourceImage;

import javafx.scene.image.Image;

public class VolumeButton extends FXPlayerButton {
	public static final Image VOLUME_BUTTON_IMAGE = loadResourceImage("/com/training/fxplayer/img/volume.png");
	public static final Image MUTE_BUTTON_IMAGE = loadResourceImage("/com/training/fxplayer/img/mute.png");

	public VolumeButton() {
		super(VOLUME_BUTTON_IMAGE);
	}
}
