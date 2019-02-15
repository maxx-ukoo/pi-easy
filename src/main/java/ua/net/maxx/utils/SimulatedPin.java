package ua.net.maxx.utils;
import java.util.EnumSet;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinProvider;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.SimulatedGpioProvider;



public class SimulatedPin extends PinProvider {

    public static final Pin GPIO_00 = createDigitalPin(0, "GPIO 0");
    public static final Pin GPIO_01 = createDigitalPin(1, "GPIO 1");
    public static final Pin GPIO_02 = createDigitalPin(2, "GPIO 2");
    public static final Pin GPIO_03 = createDigitalPin(3, "GPIO 3");
    public static final Pin GPIO_04 = createDigitalPin(4, "GPIO 4");
    public static final Pin GPIO_05 = createDigitalPin(5, "GPIO 5");
    public static final Pin GPIO_06 = createDigitalPin(6, "GPIO 6");
    public static final Pin GPIO_07 = createDigitalPin(7, "GPIO 7");
    public static final Pin GPIO_08 = createDigitalPinNoEdge(8, "GPIO 8",
            EnumSet.of(PinPullResistance.OFF, PinPullResistance.PULL_UP)); // this pin is permanently pulled up
    public static final Pin GPIO_09 = createDigitalPinNoEdge(9, "GPIO 9",
            EnumSet.of(PinPullResistance.OFF, PinPullResistance.PULL_UP)); // this pin is permanently pulled up

    protected static Pin createDigitalPin(int address, String name) {
        return createDigitalPin(SimulatedGpioProvider.NAME, address, name);
    }

    protected static Pin createDigitalPinNoEdge(int address, String name, EnumSet<PinPullResistance> resistance) {
        return createDigitalPin(SimulatedGpioProvider.NAME, address, name, resistance, EnumSet.noneOf(PinEdge.class));
    }

    protected static Pin createDigitalPinNoEdge(int address, String name) {
        return createDigitalPin(SimulatedGpioProvider.NAME, address, name, EnumSet.noneOf(PinEdge.class));
    }

    protected static Pin createDigitalAndPwmPin(int address, String name) {
        return createDigitalAndPwmPin(SimulatedGpioProvider.NAME, address, name);
    }

    protected static Pin createDigitalAndPwmPinNoEdge(int address, String name) {
        return createDigitalAndPwmPin(SimulatedGpioProvider.NAME, address, name, EnumSet.noneOf(PinEdge.class));
    }

    // *override* static method from subclass
    // (overriding a static method is not supported in Java
    //  so this method definition will hide the subclass static method)
    public static Pin getPinByName(String name) {
        return PinProvider.getPinByName(name);
    }

    // *override* static method from subclass
    // (overriding a static method is not supported in Java
    //  so this method definition will hide the subclass static method)
    public static Pin getPinByAddress(int address) {
        return PinProvider.getPinByAddress(address);
    }

    // *override* static method from subclass
    // (overriding a static method is not supported in Java
    //  so this method definition will hide the subclass static method)
    public static Pin[] allPins() { return PinProvider.allPins(); }

    // *override* static method from subclass
    // (overriding a static method is not supported in Java
    //  so this method definition will hide the subclass static method)
    public static Pin[] allPins(PinMode ... mode) { return PinProvider.allPins(mode); }
}
