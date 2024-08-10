package com.training.fxplayer.controls;

import static com.training.fxplayer.services.ImageService.loadImage;

import javafx.scene.image.Image;

public class VolumeButton extends FXPlayerButton {
	public static final Image VOLUME_BUTTON_IMAGE = loadImage("/com/training/fxplayer/img/volume.png");
	public static final Image MUTE_BUTTON_IMAGE = loadImage("/com/training/fxplayer/img/mute.png");

	public VolumeButton() {
		super(VOLUME_BUTTON_IMAGE);
	}
}
