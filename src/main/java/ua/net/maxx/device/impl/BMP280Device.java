package ua.net.maxx.device.impl;

import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.system.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.net.maxx.device.Device;
import ua.net.maxx.device.config.EnumValue;
import ua.net.maxx.device.config.StringValue;
import ua.net.maxx.device.config.Value;
import ua.net.maxx.device.lib.BME280;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
Source - https://github.com/OlivierLD/raspberry-coffee/tree/master/I2C.SPI/src/i2c/sensor
 */
public class BMP280Device implements Device {

    private static final Logger LOG = LoggerFactory.getLogger(BMP280Device.class);

    private List<Value> config;

    protected String getConfigValue(String name) {
        return config.stream().filter(value -> name.equals(value.getName()))
                .findFirst().orElse(new StringValue("", "")).getValue();
    }

    public BMP280Device() throws I2CFactory.UnsupportedBusNumberException {
        this.config = getDefaultConfig();
    }

    private List<Value> getDefaultConfig() {
        ArrayList<Value> parameters = new ArrayList<>();

        Value value = new StringValue("NUMBER_FORMAT", "##00.00");
        parameters.add(value);
        EnumValue enumValue = new EnumValue("DEVICE_ADDR", "0x77", Arrays.asList("0x76", "0x77"));
        parameters.add(enumValue);
        enumValue = new EnumValue("I2C_BUS", "1", Arrays.asList("0", "1", "2", "3", "4", "5"));
        parameters.add(enumValue);
        return parameters;
    }

    @Override
    public Iterable<Value> getConfig() {
        return this.config;
    }

    @Override
    public void setConfig(List<Value> config) {
        this.config = config;
    }

    @Override
    public String getName() {
        return "BME280 sensor";
    }

    @Override
    public String getId() {
        return "BME280";
    }

    @Override
    public Iterable<String> supportedValues() {
        return Arrays.asList("Temperature", "Pressure", "Humidity");
    }

    @Override
    public Map<String, String> getValues() {
        HashMap<String, String> values = new HashMap<String, String>();

        BME280 sensor;
        NumberFormat NF;
        try {
            String numberFormat = getConfigValue("NUMBER_FORMAT");
            NF = new DecimalFormat(numberFormat);
            Integer i2cAddress = Integer.valueOf(getConfigValue("DEVICE_ADDR"));
            Integer i2cBus = Integer.valueOf(getConfigValue("I2C_BUS"));

            sensor = new BME280(i2cAddress, i2cBus);
        } catch (Exception e) {
            LOG.error("Error prepare device", e);
            values.put("Temperature", "NaN");
            values.put("Pressure", "NaN");
            values.put("Humidity", "NaN");
            return values;
        }

        float press = 0;
        float temp = 0;
        float hum = 0;
        double alt = 0;

        try {
            temp = sensor.readTemperature();
        } catch (Exception ex) {
            LOG.error("Error read temperature", ex);
        }
        try {
            press = sensor.readPressure();
        } catch (Exception ex) {
            LOG.error("Error read pressure", ex);
        }

        try {
            hum = sensor.readHumidity();
        } catch (Exception ex) {
            LOG.error("Error read humidity", ex);
        }
        values.put("Temperature", NF.format(temp));
        values.put("Pressure", NF.format(press / 100));
        values.put("Humidity", NF.format(hum));

        return values;
    }

    @Override
    public void init() {

    }


}
