package ua.net.maxx.service;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.net.maxx.controller.ApiController;
import ua.net.maxx.device.Device;

import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class DeviceService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceService.class);

    public List<String> list() {
        try (ScanResult scanResult = new ClassGraph().whitelistPackages("ua.net.maxx.device")
                .scan()) {
            ClassInfoList widgetClasses = scanResult.getClassesImplementing("ua.net.maxx.device.Device");
            return widgetClasses.getNames().stream()
                    .map(name -> getDeviceDescription(name))
                    .filter(desc -> desc != null)
                    .collect(Collectors.toList());
        }
    }

    private String getDeviceDescription(String className) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
            Constructor<?> ctor = clazz.getConstructor();
            Device device = (Device) ctor.newInstance();
            return device.deviceName();
        } catch (Exception e) {
            LOG.error("Can't get device description", e);
        }
        return null;
    }

}
