package ua.net.maxx.controller;

import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import ua.net.maxx.controller.dto.PinSettings;
import ua.net.maxx.controller.dto.WsMessage;
import ua.net.maxx.service.GPIOSevice;
import ua.net.maxx.utils.StateChangeListener;

import java.util.function.Predicate;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ServerWebSocket("/ws")
public class WebSocketController implements StateChangeListener {

	private static final Logger LOG = LoggerFactory.getLogger(WebSocketController.class);
	
	private ObjectMapper mapper = new ObjectMapper();
	
	private WebSocketBroadcaster broadcaster;
	
	@Inject
	private GPIOSevice gpioSevice;
	
	public WebSocketController(WebSocketBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @OnOpen 
    public void onOpen(WebSocketSession session) {
    	gpioSevice.addListener(this);
        
    }

    @OnMessage 
    public void onMessage(String message, WebSocketSession session) {
    	LOG.info("onMessage" + message);
    	WsMessage wsMessage;
    	try {
    		wsMessage = mapper.readValue(message, WsMessage.class); 
    	} catch (Exception e) {
    		LOG.info("Can't parse message {}", message);
    		return;
    	}
    	
    	if ("SETMODE".contentEquals(wsMessage.type)) {
    		try {
    			PinSettings pinSettings = mapper.readValue(wsMessage.jsonContext, PinSettings.class);
				pinSettings = gpioSevice.configurePin(pinSettings);
				WsMessage response = new WsMessage();
				response.type = "SETMODESTATE";
				response.jsonContext = mapper.writeValueAsString(pinSettings);
				broadcaster.broadcastSync(mapper.writeValueAsString(response), isValid(session));
    		} catch (Exception e) {
    			LOG.info("Can't set pin mode", e);
    		}    		
    	} else if ("SETSTATE".contentEquals(wsMessage.type)) {
    		try {
    			PinSettings pinSettings = mapper.readValue(wsMessage.jsonContext, PinSettings.class);
    			pinSettings = gpioSevice.setState(pinSettings.getAddress(), pinSettings.getPinState());
				WsMessage response = new WsMessage();
				response.type = "SETMODESTATE";
				response.jsonContext = mapper.writeValueAsString(pinSettings);
				broadcaster.broadcastSync(mapper.writeValueAsString(response), isValid(session));
    		} catch (Exception e) {
    			LOG.info("Can't set pin state", e);
    		}    
    	}
    }

    @OnClose 
    public void onClose(WebSocketSession session) {

    }

    private Predicate<WebSocketSession> isValid(WebSocketSession session) {
        return s -> true;
    }

	@Override
	public void event(PinSettings settings) {
		try {
			WsMessage response = new WsMessage();
			response.type = "SETMODESTATE";
			response.jsonContext = mapper.writeValueAsString(settings);
			broadcaster.broadcastSync(mapper.writeValueAsString(response));
		} catch (JsonProcessingException e) {
			LOG.error("Can't broadcast pin state", e);
		}
	}

}