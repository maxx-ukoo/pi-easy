package ua.net.maxx.utils;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

public class GPIOCommand extends Command {

    private final Pin pin;
    private final PinState state;

    protected GPIOCommand(CommandType commandType, Pin pin, PinState state) {
        super(commandType);
        this.pin = pin;
        this.state = state;
    }

    public Pin getPin() {
        return pin;
    }

    public PinState getState() {
        return state;
    }
}
