package com.training.fxplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Player extends Application {

	/*
	 * TODO: 1. open associated files using executable of this app 2. refactoring 3.
	 * transform "errorMessageLabel" to "messageLabel" to show welcome message on
	 * opening 4. add default album cover for music files that don't have one. 5.
	 * disable controls when unsupported file opened
	 */

	public static final String APP_NAME = "FX Player";
	private static final String FXML_FILE = "player.fxml";
	private static final int INITIAL_APP_WIDTH = 960;
	private static final int INITIAL_APP_HEIGHT = 540;
	private static final String STYLES_FILE = "styles.css";
	private static final String EMPTY_STRING = "";

	private static Scene scene;
	private static Stage primaryStage;
	private static PlayerController playerController;

	@Override
	public void start(Stage primaryStage) throws Exception {
		Player.primaryStage = primaryStage;

		FXMLLoader fxmlLoader = new FXMLLoader(Player.class.getResource(FXML_FILE));
		scene = new Scene(fxmlLoader.load(), INITIAL_APP_WIDTH, INITIAL_APP_HEIGHT);
		scene.getStylesheets().add(getClass().getResource(STYLES_FILE).toExternalForm());

		playerController = fxmlLoader.getController();
		playerController.handleKeyPresses();

		// to supress "Press ESC to exit full-screen mode." hint
		primaryStage.setFullScreenExitHint(EMPTY_STRING);

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
