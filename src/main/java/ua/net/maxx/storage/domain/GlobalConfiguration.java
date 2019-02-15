package ua.net.maxx.storage.domain;

import com.pi4j.platform.Platform;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "global")
public class GlobalConfiguration {

    public GlobalConfiguration(){

    };

    public GlobalConfiguration( Platform platformType) {
        this.platformType = platformType;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "platformType", nullable = false, unique = true)
    private Platform platformType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Platform getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Platform platformType) {
        this.platformType = platformType;
    }
}
