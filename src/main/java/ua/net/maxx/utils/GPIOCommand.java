package ua.net.maxx.utils;

import com.pi4j.io.gpio.PinState;

public class GPIOCommand extends Command {

    private final Integer address;
    private final PinState state;

    protected GPIOCommand(CommandType commandType, Integer address, PinState state) {
        super(commandType);
        this.address = address;
        this.state = state;
    }

    public Integer getAddress() {
        return address;
    }

    public PinState getState() {
        return state;
    }
}
