module fxplayer {
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;

	opens com.training.fxplayer to javafx.fxml;

	exports com.training.fxplayer;
}
