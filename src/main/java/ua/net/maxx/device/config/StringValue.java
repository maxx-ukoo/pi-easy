package ua.net.maxx.device.config;

public class StringValue extends Value {

    public StringValue(String name, String value) {
        super(name, value);
    }

    @Override
    public ValueType getType() {
        return ValueType.STRING;
    }

    @Override
    public Iterable<String> options() {
        return null;
    }
}
