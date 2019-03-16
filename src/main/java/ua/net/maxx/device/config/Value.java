package ua.net.maxx.device.config;

public abstract class Value {

    private String name;
    private String value;

    public abstract ValueType getType();
    public abstract Iterable<String> options();

    public Value(String value, String name) {
        this.setName(name);
        this.setValue(value);
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return getType() + "{" +
                "value='" + value + '\'' +
                '}';
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
