package ua.net.maxx.storage.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "mqtt")
public class MQTTConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "port", nullable = false)
    private Integer port;

    @NotNull
    @Column(name = "publisherId", nullable = false)
    private String publisherId;

    @NotNull
    @Column(name = "host", nullable = false)
    private String host;
    
    private MQTTConfiguration() {
    	
    }
    

    public MQTTConfiguration(@NotNull String publisherId, @NotNull String host, @NotNull Integer port) {
        this.port = port;
        this.publisherId = publisherId;
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return "MQTTConfiguration{" +
                "id=" + id +
                ", port=" + port +
                ", publisherId='" + publisherId + '\'' +
                ", host='" + host + '\'' +
                '}';
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
