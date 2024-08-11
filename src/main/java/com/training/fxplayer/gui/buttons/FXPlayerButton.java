package com.training.fxplayer.gui.buttons;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class FXPlayerButton extends Button {

	private static final int DEFAULT_ICON_SIZE = 25;

	public FXPlayerButton(Image image) {
		this(image, DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE);
	}

	public FXPlayerButton(Image image, int fitWidth, int fitHeight) {
		ImageView imageView = new ImageView(image);
		imageView.setFitWidth(fitWidth);
		imageView.setFitHeight(fitHeight);
		setGraphic(imageView);
	}

	public void setImage(Image image) {
		((ImageView) getGraphic()).setImage(image);
	}
}
