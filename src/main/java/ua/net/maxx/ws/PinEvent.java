package ua.net.maxx.ws;

import io.micronaut.context.event.ApplicationEvent;

public class PinEvent extends ApplicationEvent {

	private static final long serialVersionUID = 5670746956807675302L;

	public PinEvent(Object source) {
		super(source);
	}

}
