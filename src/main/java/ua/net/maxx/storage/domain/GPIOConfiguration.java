package ua.net.maxx.storage.domain;

import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "gpio")
public class GPIOConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "address", nullable = false, unique = true)
    private Integer address;

    @NotNull
    @Column(name = "mode", nullable = false)
    private PinMode mode;

    @NotNull
    @Column(name = "pullup", nullable = false)
    private PinPullResistance pullUp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAddress() {
        return address;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }

    public PinMode getMode() {
        return mode;
    }

    public void setMode(PinMode mode) {
        this.mode = mode;
    }

    public PinPullResistance getPullUp() {
        return pullUp;
    }

    public void setPullUp(PinPullResistance pullUp) {
        this.pullUp = pullUp;
    }
}
