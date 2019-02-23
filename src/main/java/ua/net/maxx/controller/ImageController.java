package ua.net.maxx.controller;

import java.io.InputStream;

import javax.inject.Inject;

import com.pi4j.io.gpio.BananaPiPin;
import com.pi4j.io.gpio.BananaProPin;
import com.pi4j.io.gpio.BpiPin;
import com.pi4j.io.gpio.NanoPiPin;
import com.pi4j.io.gpio.OdroidC1Pin;
import com.pi4j.io.gpio.OrangePiPin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.platform.PlatformManager;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.server.types.files.StreamedFile;
import ua.net.maxx.service.GPIOSevice;
import ua.net.maxx.utils.SimulatedPin;

@Controller("/image")
public class ImageController {
	
	@Inject
	private GPIOSevice gpioSevice;

	@Get
	public StreamedFile download() {

		String image;
		
		switch (gpioSevice.getCurrentPlatform()) {
			case ORANGEPI: {
				image = "orange.png";
				break;
			}
			case RASPBERRYPI: {
				image = "raspberrypi.png";
				break;
			}
			default: {
				image = "default.png";
			}
		}
		InputStream inputStream = getClass().getResourceAsStream("/static/images/" + image);
		return new StreamedFile(inputStream, MediaType.IMAGE_PNG_TYPE);
	}

}
