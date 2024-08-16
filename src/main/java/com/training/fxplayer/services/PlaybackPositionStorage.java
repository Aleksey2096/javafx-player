package com.training.fxplayer.services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * This service class provides functionality to save the playback positions of previously
 * opened media files, allowing media playback to resume from where it was last stopped.
 * PlaybackPositionStorage achieves this by using a serialized map object.
 */
public class PlaybackPositionStorage implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String PLAYBACK_POSITION_STORAGE_FILE = "playbackPositions.dat";
	private static final int MAX_ENTRIES = 100;
	private static final String UNCHECKED_SUPPRESS_WARNING = "unchecked";
	private static final float PLAYBACK_POSITIONS_LOAD_FACTOR = 0.75f;

	// LinkedHashMap allows to keep entries in access order and limit size
	private Map<String, Double> playbackPositions = new LinkedHashMap<>(MAX_ENTRIES,
			PLAYBACK_POSITIONS_LOAD_FACTOR, true) {
		@Override
		protected boolean removeEldestEntry(Map.Entry<String, Double> eldest) {
			return size() > MAX_ENTRIES;
		}
	};

	public PlaybackPositionStorage() {
		loadFromFile();
	}

	public void savePosition(String mediaFilePath, double position) {
		playbackPositions.put(mediaFilePath, position);
	}

	public Double getPosition(String mediaFilePath) {
		return playbackPositions.get(mediaFilePath);
	}

	public void saveToFile() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PLAYBACK_POSITION_STORAGE_FILE))) {
			oos.writeObject(playbackPositions);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings(UNCHECKED_SUPPRESS_WARNING)
	private void loadFromFile() {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PLAYBACK_POSITION_STORAGE_FILE))) {
			playbackPositions = (Map<String, Double>) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			// Ignore if file doesn't exist yet
		}
	}
}