package ua.net.maxx.controller;

import io.micronaut.jackson.serialize.JacksonObjectSerializer;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import ua.net.maxx.controller.dto.PinSettings;
import ua.net.maxx.controller.dto.WsMessage;
import ua.net.maxx.service.GPIOSevice;

import java.io.IOException;
import java.util.function.Predicate;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ServerWebSocket("/ws")
public class WebSocketController {

	private static final Logger LOG = LoggerFactory.getLogger(WebSocketController.class);
	
	private ObjectMapper mapper = new ObjectMapper();
	
	private WebSocketBroadcaster broadcaster;
	
	@Inject
	private GPIOSevice gpioSevice;
	
	@Inject
	private JacksonObjectSerializer jacksonObjectSerializer;
	

	public WebSocketController(WebSocketBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @OnOpen 
    public void onOpen(WebSocketSession session) {
    	System.out.println(broadcaster);
    	LOG.info("onOpen");
        String msg = "Joined!";
        broadcaster.broadcast(msg, isValid(session));
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
                System.out.println("message processed: " + mapper.writeValueAsString(response));
    		} catch (Exception e) {
    			LOG.info("Can't set pin mode", e);
    		}    		
    	}
        System.out.println("message processed, exiting");
        //String msg = "[] ";
        //broadcaster.broadcast(msg, isValid(session)); 
    }

    @OnClose 
    public void onClose(WebSocketSession session) {
    	LOG.info("onCose");
        String msg = "Disconnected!";
        broadcaster.broadcast(msg, isValid( session));
    }

    private Predicate<WebSocketSession> isValid(WebSocketSession session) {
    	LOG.info("PR: " + "          " +session);
        return s -> true;
    }


}