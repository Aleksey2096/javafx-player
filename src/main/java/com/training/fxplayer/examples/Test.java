package com.training.fxplayer.examples;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Test extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("test.fxml"));
		primaryStage.setTitle("Alex's Video Player");
		primaryStage.setScene(new Scene(root, 960, 540));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
