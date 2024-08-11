module fxplayer {
	requires transitive javafx.graphics;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.base;
	requires javafx.media;
	requires jaudiotagger;

	opens com.training.fxplayer to javafx.fxml;
	opens com.training.fxplayer.gui to javafx.fxml;
	opens com.training.fxplayer.gui.views to javafx.fxml;
	opens com.training.fxplayer.gui.buttons to javafx.fxml;

	exports com.training.fxplayer;
}