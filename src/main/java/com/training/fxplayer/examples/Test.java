package com.training.fxplayer.examples;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class Test extends Application {

	private static final String APP_NAME = "FX Player";
	private static Scene scene;

	private static MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
	private static EmbeddedMediaPlayer embeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();

	@SuppressWarnings("exports")
	public static EmbeddedMediaPlayer getEmbeddedMediaPlayer() {
		return embeddedMediaPlayer;
	}

	@Override
	public void start(Stage primaryStage) {
		try {

			scene = new Scene(loadFXML("test"));
			scene.getStylesheets().add(getClass().getResource("test-styles.css").toExternalForm());

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

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		System.out.println("______executing 'stop()' method");
		embeddedMediaPlayer.controls().stop();
		embeddedMediaPlayer.release();
		mediaPlayerFactory.release();
	}

	public static void main(String[] args) {
		launch();
	}

	private static Parent loadFXML(String fxmlName) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(Test.class.getResource(fxmlName + ".fxml"));
		return fxmlLoader.load();
	}
}