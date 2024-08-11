module fxplayer {
	requires transitive javafx.graphics;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.base;
	requires javafx.media;
	requires jaudiotagger;

	opens com.training.fxplayer to javafx.fxml;

	exports com.training.fxplayer;
}