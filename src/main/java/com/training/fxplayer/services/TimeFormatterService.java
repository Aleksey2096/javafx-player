package com.training.fxplayer.services;

import javafx.util.Duration;

public class TimeFormatterService {
	private static final String FULL_TIME_FORMAT = "%d:%02d:%02d";
	private static final String SHORT_TIME_FORMAT = "%d:%02d";
	private static final int NUMBER_OF_MINUTES_IN_HOUR = 60;

	public static String formatTime(Duration duration) {
		int hours = (int) duration.toHours();
		int minutes = (int) duration.toMinutes() % NUMBER_OF_MINUTES_IN_HOUR;
		int seconds = (int) duration.toSeconds() % NUMBER_OF_MINUTES_IN_HOUR;

		if (hours > 0) {
			return String.format(FULL_TIME_FORMAT, hours, minutes, seconds);
		} else {
			return String.format(SHORT_TIME_FORMAT, minutes, seconds);
		}
	}
}
