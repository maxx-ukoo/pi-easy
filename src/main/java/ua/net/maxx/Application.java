package ua.net.maxx;

import com.pi4j.io.gpio.BananaPiPin;
import com.pi4j.io.gpio.BananaProPin;
import com.pi4j.io.gpio.BpiPin;
import com.pi4j.io.gpio.NanoPiPin;
import com.pi4j.io.gpio.OdroidC1Pin;
import com.pi4j.io.gpio.OrangePiPin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.platform.PlatformManager;

import io.micronaut.runtime.Micronaut;

public class Application {

    public static void main(String[] args) throws PlatformAlreadyAssignedException {
        PlatformManager.setPlatform(Platform.SIMULATED);

        Micronaut.run(Application.class);
    }

    public static void preparePins() {
        switch (PlatformManager.getPlatform()) {
            case BANANAPI: {
            	BananaPiPin.allPins();
            	break;
            }
            case BANANAPRO: {
            	BananaProPin.allPins();
            	break;
            }
            case BPI: {
                BpiPin.allPins();
                break;
            }
            case ODROID: {
                OdroidC1Pin.allPins();
                break;
            }
            case ORANGEPI: {
                OrangePiPin.allPins();
                break;
            }
            case NANOPI: {
                NanoPiPin.allPins();
                break;
            }
            case SIMULATED: {
                break;
            }
            default: {
                // if a platform cannot be determine, then assume it's the default RaspberryPi
            	RaspiPin.allPins();
            }
        }
    }
}