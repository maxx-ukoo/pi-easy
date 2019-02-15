package ua.net.maxx.utils;

public abstract class Command {
    private final CommandType commandType;

    protected Command(CommandType commandType) {
        this.commandType = commandType;
    }

    public CommandType getCommandType() {
        return commandType;
    }
}
