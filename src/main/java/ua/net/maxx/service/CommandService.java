package ua.net.maxx.service;

import ua.net.maxx.utils.Command;
import ua.net.maxx.utils.CommandParser;
import ua.net.maxx.utils.GPIOCommand;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.gpio.PinState;

@Singleton
public class CommandService {
	
	private static final Logger LOG = LoggerFactory.getLogger(MQTTService.class);

	private final GPIOSevice gpioSevice;
	
    @Inject
    public CommandService(GPIOSevice gpioSevice) {
    	this.gpioSevice = gpioSevice;
    }

    public void executeCommand(@NotBlank String cmd) {
    	LOG.debug("Received command: {}", cmd);
        Command command = CommandParser.parseCommand(cmd);
        switch (command.getCommandType()) {
            case GPIO:
            	GPIOCommand gpioCommand = (GPIOCommand) command;
            	gpioSevice.setState(gpioCommand.getAddress(), gpioCommand.getState());
            	return;
        }
        throw new IllegalStateException("Unsupported command: " + cmd);
    }
    
}
