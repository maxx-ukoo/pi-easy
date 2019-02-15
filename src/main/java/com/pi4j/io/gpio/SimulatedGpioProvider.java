package com.pi4j.io.gpio;


import java.util.Map;

import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.GpioProviderBase;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiGpioProvider;

public class SimulatedGpioProvider extends GpioProviderBase implements GpioProvider {

    // We use the name of the platform that we are simulating.
    public static String NAME;

    public SimulatedGpioProvider() {
        Map<String, String> env = System.getenv();

        String config = env.get("SimulatedPlatform");

        // If no specific platform is specified we default to simulating the raspberry pi
        if (config == null) {
            NAME = RaspiGpioProvider.NAME;
        }

        //NAME = config;
    }

    @Override
    public String getName() {
        return NAME;
    }

    public void setState(Pin pin, PinState state) {
        // cache pin state
        getPinCache(pin).setState(state);

        // dispatch event
        dispatchPinDigitalStateChangeEvent(pin, state);
    }

    public void setAnalogValue(Pin pin, double value) {
        // cache pin state
        getPinCache(pin).setAnalogValue(value);

        // dispatch event
        dispatchPinAnalogValueChangeEvent(pin, value);
    }

}
