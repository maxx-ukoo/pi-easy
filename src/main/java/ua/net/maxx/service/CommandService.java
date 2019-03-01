package ua.net.maxx.service;

import ua.net.maxx.utils.Command;
import ua.net.maxx.utils.CommandParser;
import ua.net.maxx.utils.GPIOCommand;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class CommandService {
	
	private static final Logger LOG = LoggerFactory.getLogger(MQTTService.class);

	private final GPIOSevice gpioSevice;
	
    @Inject
    public CommandService(GPIOSevice gpioSevice) {
    	this.gpioSevice = gpioSevice;
    }

    public String executeCommand(@NotBlank String cmd) {
    	LOG.info("Received command: {}", cmd);
        Command command = CommandParser.parseCommand(cmd);
        switch (command.getCommandType()) {
            case GPIO:
                return gpioSevice.executeCommand((GPIOCommand) command);
        }
        throw new IllegalStateException("Unsupported command: " + cmd);
    }
    
}
