package com.training.fxplayer.controls;

import java.io.File;

import javafx.stage.FileChooser;
import javafx.stage.Window;

public class FileSelector {
	private static final String INITIAL_DIRECTORY = "D:/My documents/Downloads";
	private static final String EXTENSION_FILTER_DESCRIPTION = "Video and Music Files";
	private static final String AUXILIARY_EXTENSION_FILTER_DESCRIPTION = "All Files";
	private static final String[] SUPPORTED_EXTENSIONS = { "*.mp4", "*.flv", "*.mp3", "*.m4a" };
	private static final String ALL_EXTENSIONS = "*.*";
	private static final FileChooser FILE_CHOOSER = new FileChooser();
	private final Window ownerWindow;

	public FileSelector(Window ownerWindow) {
		this.ownerWindow = ownerWindow;

		FILE_CHOOSER.setInitialDirectory(new File(INITIAL_DIRECTORY));
		FILE_CHOOSER.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter(EXTENSION_FILTER_DESCRIPTION, SUPPORTED_EXTENSIONS),
				new FileChooser.ExtensionFilter(AUXILIARY_EXTENSION_FILTER_DESCRIPTION, ALL_EXTENSIONS));
	}

	public File showOpenDialog() {
		return FILE_CHOOSER.showOpenDialog(ownerWindow);
	}
}
