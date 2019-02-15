package ua.net.maxx.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import ua.net.maxx.service.CommandService;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;

@Controller("/control")
public class HTTPController {

    private final CommandService httpService;

    @Inject
    public HTTPController(CommandService httpService) {
        this.httpService = httpService;
    }

    @Get
    public void cmd(@NotBlank @QueryValue("cmd") String cmd) {
        httpService.executeCommand(cmd);
    }

}
