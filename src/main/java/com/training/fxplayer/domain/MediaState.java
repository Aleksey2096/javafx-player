package com.training.fxplayer.domain;

public class MediaState {
	private static final double INITIAL_VOLUME = 0.7;
	private boolean isEndOfMedia = false;
	private double previousVolume = INITIAL_VOLUME;
	private boolean isMuted = false;

	public boolean isEndOfMedia() {
		return isEndOfMedia;
	}

	public void setEndOfMedia(boolean isEndOfMedia) {
		this.isEndOfMedia = isEndOfMedia;
	}

	public double getPreviousVolume() {
		return previousVolume;
	}

	public void setPreviousVolume(double previousVolume) {
		this.previousVolume = previousVolume;
	}

	public boolean isMuted() {
		return isMuted;
	}

	public void setMuted(boolean isMuted) {
		this.isMuted = isMuted;
	}
}
