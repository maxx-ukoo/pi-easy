package ua.net.maxx.controller;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.server.types.files.StreamedFile;
import ua.net.maxx.service.GPIOSevice;

import javax.inject.Inject;
import java.io.InputStream;

@Controller("/image")
public class ImageController {

    private final GPIOSevice gpioSevice;

    @Inject
    public ImageController(GPIOSevice gpioSevice) {
        this.gpioSevice = gpioSevice;
    }

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
