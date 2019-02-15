package ua.net.maxx.events;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;

import java.util.HashMap;
import java.util.Map;

public class PinEvent {

	private PinEventType eventType;
    private String name;
    private Integer address;
    private PinState state;
    private Map<String, String> other = new HashMap<String, String>();

    @JsonAnyGetter
    public Map<String, String> any() {
        return other;
    }

    @JsonAnySetter
    public void set(String name, String value) {
        other.put(name, value);
    }

    private PinEvent() {

    }

    public static Builder newBuilder() {
        return new PinEvent().new Builder();
    }

    public PinEventType getEventType() {
        return eventType;
    }

    public String getName() {
        return name;
    }

    public PinState getState() {
        return state;
    }

    public Integer getAddress() {
        return address;
    }

    public class Builder {

        private Builder() {

        }

        public Builder setPinEventType(PinEventType eventType) {
            PinEvent.this.eventType = eventType;
            return this;
        }

        public Builder setPinState(PinState state) {
            PinEvent.this.state = state;
            return this;
        }

        public Builder setPinName(String name) {
            PinEvent.this.name = name;
            return this;
        }

        public Builder setAddress(Integer address) {
            PinEvent.this.address = address;
            return this;
        }
        
		public Builder addValue(String key, Object value) {
			PinEvent.this.other.put(key, value.toString());
			return this;
		}

        public PinEvent build() {
            PinEvent pinEvent = new PinEvent();
            pinEvent.eventType = PinEvent.this.eventType;
            pinEvent.state = PinEvent.this.state;
            pinEvent.name = PinEvent.this.name;
            pinEvent.address = PinEvent.this.address;
            pinEvent.other = PinEvent.this.other;
            return pinEvent;
        }

    }

    @Override
	public String toString() {
		return "PinEvent [eventType=" + eventType + ", name=" + name + ", address=" + address + ", state=" + state
				+ ", other=" + other + "]";
	}

}
