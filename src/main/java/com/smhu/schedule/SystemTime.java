package com.smhu.schedule;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SystemTime {

    public static long SYSTEM_TIME = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"), new Locale("vi", "vn"))
            .getTimeInMillis();

    @Scheduled(fixedRate = 1000)
    public void runSystemTime() {
        SYSTEM_TIME += 1000;
    }
}
