package com.training.fxplayer.gui;

import javafx.scene.control.Slider;

public class VolumeSlider extends Slider {

	public VolumeSlider() {
		setVisibility(false);
	}

	public void setVisibility(boolean isVisible) {
		setVisible(isVisible);
		setManaged(isVisible);
	}
}
