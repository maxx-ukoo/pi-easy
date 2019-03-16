package ua.net.maxx;

import org.junit.Test;
import ua.net.maxx.service.DeviceService;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class DeviceServiceTest {

    @Test
    public void deviceList() {
        DeviceService ds = new DeviceService();
        List<String> list = ds.list();
        System.out.println(list);
        assertEquals(1, list.size());
    }

}
