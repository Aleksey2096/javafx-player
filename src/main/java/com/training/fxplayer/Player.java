package com.training.fxplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Player extends Application {

	/*
	 * TODO: 1. close media player before opening next media file 2. add filters to
	 * FILE_CHOOSER (file types, default directory) 3. show album covers for music
	 * files 4. open associated files using executable of this app
	 */

	public static final String APP_NAME = "FX Player";

	private static Scene scene;
	private static Stage primaryStage;
	private static PlayerController playerController;

	@Override
	public void start(Stage primaryStage) throws Exception {
		Player.primaryStage = primaryStage;

		FXMLLoader fxmlLoader = new FXMLLoader(Player.class.getResource("player.fxml"));
		scene = new Scene(fxmlLoader.load(), 960, 540);
		scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

		playerController = fxmlLoader.getController();
		playerController.handleKeyPresses();

		// to supress "Press ESC to exit full-screen mode." hint
		primaryStage.setFullScreenExitHint("");

		// puts this app's window on top of other windows
		primaryStage.setAlwaysOnTop(true);

		setAppTitle(APP_NAME);
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
		boolean wasFullScreen = primaryStage.isFullScreen();
		primaryStage.setFullScreen(!wasFullScreen);

		// reapplies the always-on-top property after exiting fullscreen and turns it
		// off after entering fullscreen
		primaryStage.setAlwaysOnTop(wasFullScreen);
	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}
}
