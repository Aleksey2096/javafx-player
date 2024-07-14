package com.training.fxplayer;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class Player extends Application {

	private static final String APP_NAME = "FX Player";
	private static Scene scene;

	@Override
	public void start(Stage primaryStage) throws Exception {
		scene = new Scene(loadFXML("player"));
		scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

		scene.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2) {
					primaryStage.setFullScreen(!primaryStage.isFullScreen());
				}
			}
		});

		primaryStage.setTitle(APP_NAME);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch();
	}

	private static Parent loadFXML(String fxmlName) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(Player.class.getResource(fxmlName + ".fxml"));
		return fxmlLoader.load();
	}
}
