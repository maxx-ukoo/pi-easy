package ua.net.maxx.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import ua.net.maxx.storage.domain.GPIOConfiguration;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PinSettings {

	private final int address;
	private final String name;
	private final PinMode pinMode;
	private final PinPullResistance pullResistance;
	
	@JsonProperty("pinState")
	private PinState pinState;

	public static PinSettings fromGpioPin(GpioPin pin) {
		return new PinSettings(pin.getPin().getAddress(), pin.getName(), pin.getMode(), pin.getPullResistance());
	}

	@JsonCreator
	public PinSettings(@JsonProperty("address") int address, @JsonProperty("name") String name,
			@JsonProperty("pinMode") PinMode pinMode,
			@JsonProperty("pullResistance") PinPullResistance pullResistance) {
		this.address = address;
		this.name = name;
		this.pinMode = pinMode;
		this.pullResistance = pullResistance;
	}

	public static PinSettings fromPinConfig(GPIOConfiguration pinConfig) {
		return new PinSettings(pinConfig.getAddress(), pinConfig.getName(), pinConfig.getMode(), pinConfig.getPullUp());
	}

	public int getAddress() {
		return address;
	}

	public String getName() {
		return name;
	}

	public PinMode getPinMode() {
		return pinMode;
	}

	public PinPullResistance getPullResistance() {
		return pullResistance;
	}

	public PinState getPinState() {
		return pinState;
	}

	public void setPinState(PinState pinState) {
		this.pinState = pinState;
	}



}
