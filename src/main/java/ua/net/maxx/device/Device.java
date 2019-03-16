package ua.net.maxx.device;

import ua.net.maxx.device.config.Value;

import java.util.List;
import java.util.Map;

public interface Device {
    String deviceName();
    Iterable<String> supportedValues();
    Map<String, String> getValues();
    void init();
    Iterable<Value> getConfig();
    void setConfig(List<Value> config);
}
