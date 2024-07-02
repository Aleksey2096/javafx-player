module fxplayer {
	requires transitive javafx.graphics;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.media;

	opens com.training.fxplayer to javafx.fxml;
	opens com.training.fxplayer.examples to javafx.fxml;

	exports com.training.fxplayer;
	exports com.training.fxplayer.examples;
}