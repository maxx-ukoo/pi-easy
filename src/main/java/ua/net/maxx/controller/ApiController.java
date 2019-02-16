package ua.net.maxx.controller;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import com.pi4j.io.gpio.Pin;
import com.pi4j.platform.PlatformManager;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import ua.net.maxx.service.GPIOSevice;

@Controller("/api")
public class ApiController {
	
	
	@Inject
	private GPIOSevice gpioSevice;
	
    @Get("/sysinfo")
    public Map<String, String>  index() {
    	Map<String, String> info = new HashMap<>();
    	info.put("platformName",  PlatformManager.getPlatform().getLabel());
    	info.put("platformID",  PlatformManager.getPlatform().getId());
    	return info;
    }
    
    @Get("/pins")
    public Pin[]  pins() {
    	return gpioSevice.allPins();
    }

}
