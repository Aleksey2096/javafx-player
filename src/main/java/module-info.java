module fxplayer {
	requires transitive javafx.graphics;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.media;

	opens com.training.fxplayer to javafx.fxml;

	exports com.training.fxplayer;
	exports com.training.fxplayer.examples;
}