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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.discovery.event.ServiceStartedEvent;
import io.micronaut.scheduling.annotation.Async;
import ua.net.maxx.dto.MQTTSettings;
import ua.net.maxx.events.PinEvent;
import ua.net.maxx.storage.domain.MQTTConfiguration;
import ua.net.maxx.storage.service.StorageService;
import ua.net.maxx.events.StateChangeListener;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.ArrayList;
import java.util.List;


@Singleton
public class MQTTService implements MqttCallback, ApplicationEventListener<ServiceStartedEvent>, StateChangeListener<PinEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(MQTTService.class);

	private static final String TOPIC = "/cmd";

	private final List<IMqttClient> publishers = new ArrayList<>();
	private final ObjectMapper mapper = new ObjectMapper();

	private final StorageService storageService;
	private final CommandService commandService;
	private final GPIOSevice gpioSevice;

	@Async
	@Override
	public void onApplicationEvent(final ServiceStartedEvent event) {
		initialize();
	}

	@Inject
	public MQTTService(StorageService storageService, CommandService commandService, GPIOSevice gpioSevice) {
		this.storageService = storageService;
		this.commandService = commandService;
		this.gpioSevice = gpioSevice;
	}

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
			LOG.info("Initializing MQTT brocker: {}", config);
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
		gpioSevice.addListener(this);
	}

	private String getSubscriberTopic(MQTTConfiguration config) {
		return "/" + config.getPublisherId() + TOPIC;
	}

	public void sendMessage(String topic, MqttMessage msg) {
		publishers.stream().forEach(client -> {
			try {
				if (client.isConnected()) {
					msg.setQos(1);
					msg.setRetained(true);
					client.publish("/" + client.getClientId() + topic, msg);
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
		LOG.debug("Connection lost", cause);
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		LOG.debug(String.format("Message arrived: [%s] %s", topic, new String(message.getPayload())));
		commandService.executeCommand(new String(message.getPayload()));
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {

	}

	@Override
	public void event(PinEvent event) {
		try {
			byte[] payload = mapper.writeValueAsString(event).getBytes();
			sendMessage("/" + String.valueOf(event.getAddress()), new MqttMessage(payload));
		} catch (JsonProcessingException e) {
			LOG.error("Can't broadcast pin state", e);
		}
	}
}
