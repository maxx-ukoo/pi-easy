package ua.net.maxx.service;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.context.annotation.Context;
import ua.net.maxx.controller.dto.MQTTSettings;
import ua.net.maxx.storage.domain.MQTTConfiguration;
import ua.net.maxx.storage.service.StorageService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

//https://www.baeldung.com/java-mqtt-client

@Singleton
public class MQTTService implements MqttCallback {

	private static final Logger LOG = LoggerFactory.getLogger(MQTTService.class);
	
	private static final String TOPIC = "/cmd";

	private final List<IMqttClient> publishers = new ArrayList<>();

	private final StorageService storageService;
	private final CommandService commandService;

	@Inject
	public MQTTService(StorageService storageService, CommandService commandService) {
		this.storageService = storageService;
		this.commandService = commandService;
	}

	@PostConstruct
	private void initialize() {
		publishers.stream().forEach(publisher -> {
			try {
				publisher.close();
			} catch (MqttException e) {
				LOG.error("Error on shutdown publisher", e);
			}
		});

		List<MQTTConfiguration> list = storageService.getMQTTConfigs();
		list.stream().forEach(config -> {
			String publisherId = config.getPublisherId();
			String host = config.getHost();
			Integer port = config.getPort();
			try {
				MqttClient publisher = new MqttClient(String.format("tcp://%s:%d", host, port), publisherId);
				publishers.add(publisher);
				MqttConnectOptions options = new MqttConnectOptions();
				options.setAutomaticReconnect(true);
				options.setCleanSession(true);
				options.setConnectionTimeout(10);
				publisher.setCallback(this);
				publisher.connect(options);
				publisher.subscribe(getSubscriberTopic(config));
			} catch (MqttException e) {
				LOG.error("Error creating MQTT client", e);
			}
		});
	}
	
	private String getPublisherTopic(String id) {
		return "/"+ id;
	}
	
	private String getSubscriberTopic(MQTTConfiguration config) {
		return "/"+ config.getPublisherId() + TOPIC;
	}

	public void sendMessage(String topic, MqttMessage msg) {
		publishers.stream().forEach(client -> {
			try {
				if (client.isConnected()) {
					msg.setQos(0);
					msg.setRetained(true);
					client.publish(getPublisherTopic(client.getClientId()), msg);
				}
			} catch (MqttException e) {
				LOG.error("Error on shutdown publisher", e);
			}
		});
	}

	public List<MQTTConfiguration> getMQTTConfig() {
		return storageService.getMQTTConfigs();
	}

	public MQTTConfiguration updateMQTTConfig(MQTTSettings mqttConfig) {
		MQTTConfiguration config = storageService.updateMQTTConfig(mqttConfig);
		initialize();
		return config;
	}

	public void deleteMQTTConfig(Long id) {
		storageService.deleteMQTTConfig(id);
		initialize();
	}

	@Override
	public void connectionLost(Throwable cause) {
		LOG.info("Connection lost", cause);
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		LOG.info(String.format("[%s] %s", topic, new String(message.getPayload())));
		commandService.executeCommand(new String(message.getPayload()));
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {

	}
}
