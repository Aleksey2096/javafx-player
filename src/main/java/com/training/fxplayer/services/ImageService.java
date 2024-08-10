package com.training.fxplayer.services;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

import javafx.scene.image.Image;

public class ImageService {
	public static Image loadImage(String resourcePath) {
		return new Image(ImageService.class.getResourceAsStream(resourcePath));
	}

	public static Image extractAlbumCover(File file) {
		try {
			Tag tag = AudioFileIO.read(file).getTag();
			if (tag != null) {
				Artwork artwork = tag.getFirstArtwork();
				if (artwork != null) {
					byte[] imageData = artwork.getBinaryData();
					return new Image(new ByteArrayInputStream(imageData));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
