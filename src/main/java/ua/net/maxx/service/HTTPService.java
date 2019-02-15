package ua.net.maxx.service;

import ua.net.maxx.utils.Command;
import ua.net.maxx.utils.CommandParser;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;

@Singleton
public class HTTPService {

    @Inject
    GPIOSevice gpioSevice;

    public String executeCommand(@NotBlank String cmd) {
        Command command = CommandParser.parseCommand(cmd);
        switch (command.getCommandType()) {
            case GPIO:
                return gpioSevice.executeCommand(command);
        }
        throw new IllegalStateException("Unsupported command: " + cmd);
    }
}
