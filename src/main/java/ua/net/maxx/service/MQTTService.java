package ua.net.maxx.service;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.net.maxx.storage.domain.MQTTConfiguration;
import ua.net.maxx.storage.service.StorageService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

//https://www.baeldung.com/java-mqtt-client

@Singleton
public class MQTTService {

    private static final Logger LOG = LoggerFactory.getLogger(MQTTService.class);

    private final List<IMqttClient> publishers = new ArrayList<>();

    private final StorageService storageService;

    @Inject
    public MQTTService(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostConstruct
    private void initialize() {
        publishers.stream().forEach(publisher -> {
            try {
                publisher.disconnect();
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
                publisher.connect(options);
            } catch (MqttException e) {
                LOG.error("Error creating MQTT client", e);
            }
        });
    }


    public List<MQTTConfiguration> getMQTTConfig() {
        return storageService.getMQTTConfigs();
    }

    public MQTTConfiguration updateMQTTConfig(MQTTConfiguration mqttConfig) {
        mqttConfig = storageService.upfateMQTTConfig(mqttConfig);
        initialize();
        return mqttConfig;
    }
}
