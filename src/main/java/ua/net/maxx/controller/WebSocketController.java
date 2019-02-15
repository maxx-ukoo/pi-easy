package ua.net.maxx.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.net.maxx.dto.PinSettings;
import ua.net.maxx.events.PinEvent;
import ua.net.maxx.events.StateChangeListener;
import ua.net.maxx.service.GPIOSevice;

import javax.inject.Inject;
import java.util.function.Predicate;

@ServerWebSocket("/ws")
public class WebSocketController implements StateChangeListener<PinEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketController.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private final WebSocketBroadcaster broadcaster;
    private final GPIOSevice gpioSevice;

    @Inject
    public WebSocketController(WebSocketBroadcaster broadcaster, GPIOSevice gpioSevice) {
        this.gpioSevice = gpioSevice;
        this.broadcaster = broadcaster;
        gpioSevice.addListener(this);
    }


    @OnOpen
    public void onOpen(WebSocketSession session) {

    }

    @OnMessage
    public void onMessage(String message, WebSocketSession session) {
        LOG.debug("onMessage" + message);
        PinEvent event;
        try {
            event = mapper.readValue(message, PinEvent.class);
        } catch (Exception e) {
            LOG.error("Can't parse message {}", message);
            return;
        }
        LOG.debug("Event: " + event);
        switch (event.getEventType()) {
            case CHANGEMODE:
                try {
                    PinSettings pinSettings = PinSettings.fromEvent(event);
                    gpioSevice.configurePin(pinSettings);
                } catch (Exception e) {
                    LOG.error("Can't set pin mode for event: {}", message, e);
                }
                break;
            case CHANGESTATE:
                try {
                    gpioSevice.setState(event.getAddress(), event.getState());
                } catch (Exception e) {
                    LOG.error("Can't set pin state for message: {}", message, e);
                }
                break;
            case INFO:
                break;
            default:
                LOG.warn("Unsupported message received, message: {}", message);
        }
    }

    @OnClose
    public void onClose(WebSocketSession session) {
    	gpioSevice.removeListener(this);
    }

    private Predicate<WebSocketSession> isValid(WebSocketSession session) {
        return s -> true;
    }

    @Override
    public void event(PinEvent event) {
        try {
            broadcaster.broadcastSync(mapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            LOG.error("Can't broadcast pin state", e);
        }
    }

}