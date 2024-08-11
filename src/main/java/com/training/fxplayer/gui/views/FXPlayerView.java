package com.training.fxplayer.gui.views;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Scene;

public interface FXPlayerView {
	String WIDTH_PROPERTY = "width";
	String HEIGHT_PROPERTY = "height";

	DoubleProperty fitWidthProperty();

	DoubleProperty fitHeightProperty();

	ReadOnlyObjectProperty<Scene> sceneProperty();

	void setPreserveRatio(boolean isPreserveRatio);

	default void fitIntoScene(boolean isPreserveRatio) {
		DoubleProperty widthProperty = fitWidthProperty();
		DoubleProperty hightProperty = fitHeightProperty();

		widthProperty.bind(Bindings.selectDouble(sceneProperty(), WIDTH_PROPERTY));
		hightProperty.bind(Bindings.selectDouble(sceneProperty(), HEIGHT_PROPERTY));

		setPreserveRatio(isPreserveRatio);
	}
}
