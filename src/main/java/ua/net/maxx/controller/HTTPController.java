package ua.net.maxx.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import ua.net.maxx.service.HTTPService;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;

@Controller("/control")
public class HTTPController {

    @Inject
    private HTTPService httpService;

    @Get
    public void cmd(@NotBlank @QueryValue("cmd") String cmd) {
        httpService.executeCommand(cmd);
    }

}
