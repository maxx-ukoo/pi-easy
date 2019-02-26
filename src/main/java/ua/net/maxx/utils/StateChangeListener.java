package ua.net.maxx.utils;

import com.pi4j.io.gpio.PinState;

import ua.net.maxx.controller.dto.PinSettings;

public interface StateChangeListener {
	
	public void event(PinSettings settings);

}
