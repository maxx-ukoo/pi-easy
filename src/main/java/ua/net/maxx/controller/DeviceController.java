package ua.net.maxx.controller;


import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.net.maxx.service.DeviceService;

import javax.inject.Inject;

@Controller("/device")
public class DeviceController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiController.class);
    private final DeviceService deviceService;

    @Inject
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Get
    public Iterable<String> list() {
        return deviceService.list();
    }
}
