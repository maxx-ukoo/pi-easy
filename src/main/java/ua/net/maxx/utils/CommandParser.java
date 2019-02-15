package ua.net.maxx.utils;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class CommandParser {

    private static final String COMMAND_GPIO = "GPIO";

    public static Command parseCommand(String cmd) {
        if (cmd.startsWith(COMMAND_GPIO)) {
            return parseGPICommand(cmd);
        }
        throw new IllegalStateException("Unsupported command: " + cmd);
    }

    private static Command parseGPICommand(String cmd) {
        String[] str = cmd.split(",");
        return new GPIOCommand(CommandType.GPIO, RaspiPin.getPinByAddress(Integer.valueOf(str[1])), PinState.valueOf(str[2]));
    }
}
