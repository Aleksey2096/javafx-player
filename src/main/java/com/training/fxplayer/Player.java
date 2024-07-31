package com.training.fxplayer;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Player extends Application {

	public static final String APP_NAME = "FX Player";
	private static Scene scene;

	private static Stage primaryStage;

	@Override
	public void start(Stage primaryStage) throws Exception {
		Player.primaryStage = primaryStage;

		scene = new Scene(loadFXML("player"), 960, 540);
		scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

		// To supress "Press ESC to exit full-screen mode." hint
		primaryStage.setFullScreenExitHint("");

		Player.setAppTitle(APP_NAME);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch();
	}

	public static void setAppTitle(String title) {
		primaryStage.setTitle(title);
	}

	public static void toggleFullScreen() {
		primaryStage.setFullScreen(!primaryStage.isFullScreen());
	}

	private static Parent loadFXML(String fxmlName) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(Player.class.getResource(fxmlName + ".fxml"));
		return fxmlLoader.load();
	}
}
