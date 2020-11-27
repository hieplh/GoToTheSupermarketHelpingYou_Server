package com.smhu.schedule;

import com.smhu.core.CoreFunctions;
import com.smhu.iface.ICore;
import com.smhu.system.SystemTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scan {

    public static boolean ENABLE_SCAN_SCHEDULE = false;
    private final ICore coreListener;

    public Scan() {
        coreListener = new CoreFunctions();
    }

//    @Scheduled(fixedRate = 15 * 1000)
    public void removeOrder() {
        SystemTime systemTime = new SystemTime();
        systemTime.checkOrderOutOfTimeRelease();
    }
    
    @Scheduled(fixedRate = 3 * 1000)
    public void scanShipper() {
        coreListener.scanShippers();
    }
    
    @Scheduled(fixedRate = 20 * 1000)
    public void scanOrder() {
        if (ENABLE_SCAN_SCHEDULE) {
            coreListener.scanOrder();
        }
    }
}
