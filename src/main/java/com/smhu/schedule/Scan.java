package com.smhu.schedule;

import com.smhu.core.CoreFunctions;
import com.smhu.iface.ICore;
import com.smhu.system.SystemTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scan {

    public static boolean ENABLE_SCAN_SCHEDULE = false;
    public static boolean ENABLE_REMOTE_SCHEDULE = false;

    final ICore coreListener;
    final SystemTime systemTime;

    public Scan() {
        coreListener = new CoreFunctions();
        systemTime = new SystemTime();
    }

//    @Scheduled(fixedRate = 15 * 1000)
    public void removeOrder() {
        if (ENABLE_REMOTE_SCHEDULE) {
            systemTime.checkOrderOutOfTimeRelease();
        }
    }

    @Scheduled(fixedDelay = 3 * 1000)
    public void scanShipper() {
        coreListener.scanShippers();
    }

    @Scheduled(fixedDelay = 20 * 1000)
    public void scanOrder() {
        if (ENABLE_SCAN_SCHEDULE) {
            coreListener.scanOrder();
        }
    }
}
