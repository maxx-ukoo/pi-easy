package ua.net.maxx.controller;

import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;

import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerWebSocket("/ws/{pin}")
public class WebSocketController {

	private static final Logger LOG = LoggerFactory.getLogger(WebSocketController.class);
	
	private WebSocketBroadcaster broadcaster;

	public WebSocketController(WebSocketBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @OnOpen 
    public void onOpen(String pin, WebSocketSession session) {
    	System.out.println(broadcaster);
    	LOG.info("onOpen" + pin);
        String msg = "[" + pin + "] Joined!";
        broadcaster.broadcast(msg, isValid(pin, session));
    }

    @OnMessage 
    public void onMessage(String pin, String message, WebSocketSession session) {
    	LOG.info("onMessage" + pin + " " + message);
        String msg = "[" + pin + "] ";
        broadcaster.broadcast(msg, isValid(pin, session)); 
    }

    @OnClose 
    public void onClose( String pin, WebSocketSession session) {
    	LOG.info("onCose" + pin);
        String msg = "[" + pin + "] Disconnected!";
        broadcaster.broadcast(msg, isValid(pin, session));
    }

    private Predicate<WebSocketSession> isValid(String topic, WebSocketSession session) {
    	LOG.info("PR: " + topic + "          " +session);
        return s -> s != session;
    }


}