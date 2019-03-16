package ua.net.maxx.device.config;

public class EnumValue extends Value {

    private Iterable<String> options;

    public EnumValue(String name, String value, Iterable<String> options) {
        super(value, name);
        this.options = options;
    }

    @Override
    public ValueType getType() {
        return ValueType.ENUM;
    }

    @Override
    public Iterable<String> options() {
        return this.options;
    }
}
