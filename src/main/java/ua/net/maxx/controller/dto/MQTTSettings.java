package ua.net.maxx.controller.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pi4j.io.gpio.GpioPin;

import ua.net.maxx.storage.domain.MQTTConfiguration;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MQTTSettings {

	private Long id;
	private Integer port;
	private String publisherId;
	private String host;

	public static PinSettings fromGpioPin(GpioPin pin) {
		return new PinSettings(pin.getPin().getAddress(), pin.getName(), pin.getMode(), pin.getPullResistance());
	}

	@JsonCreator
	public MQTTSettings(@JsonProperty("id") Long id, @JsonProperty("publisherId") String publisherId,
			@JsonProperty("host") String host, @JsonProperty("port") Integer port) {
		this.setId(id);
		this.setPublisherId(publisherId);
		this.setPort(port);
		this.setHost(host);
	}

	public static MQTTSettings fromMQTTConfig(MQTTConfiguration mqtt) {
		return new MQTTSettings(mqtt.getId(), mqtt.getPublisherId(), mqtt.getHost(), mqtt.getPort());
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(String publisherId) {
		this.publisherId = publisherId;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	@Override
	public String toString() {
		return "MQTTSettings [id=" + id + ", port=" + port + ", publisherId=" + publisherId + ", host=" + host + "]";
	}

}
