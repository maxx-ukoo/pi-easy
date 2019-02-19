package ua.net.maxx.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;

public class PinSettings {
	
    private final int address;
	private final String name;
    private final PinMode pinMode;
    private final PinPullResistance pullResistance;
    
    @JsonCreator
    public PinSettings(@JsonProperty("address") int address,
    		@JsonProperty("name") String name,
    		@JsonProperty("pinMode")PinMode pinMode,
    		@JsonProperty("pullResistance") PinPullResistance pullResistance) {
		this.address = address;
		this.name = name;
		this.pinMode = pinMode;
		this.pullResistance = pullResistance;
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
    
}
