package com.smhu.controller;

import com.smhu.system.SystemTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@RequestMapping("/api")
public class SystemTimeController {

    SystemTimeService service;

    @GetMapping("/system/{time}")
    public ResponseEntity updateSystemTime(@PathVariable("time") String time) {
        service = new SystemTimeService();
        ZonedDateTime dateTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(dateTime.getYear(), dateTime.getMonthValue() - 1, dateTime.getDayOfMonth(),
                service.getTime(time, service.HOUR),
                service.getTime(time, service.MINUTE),
                service.getTime(time, service.SECOND));

        SystemTime.SYSTEM_TIME = calendar.getTimeInMillis();
        return new ResponseEntity(HttpStatus.OK);
    }

    class SystemTimeService {

        final int HOUR = 1;
        final int MINUTE = 2;
        final int SECOND = 3;

        int getTime(String time, int type) {
            String[] timeArr = time.split(":");
            switch (type) {
                case HOUR:
                    return Integer.parseInt(timeArr[0]);
                case MINUTE:
                    return Integer.parseInt(timeArr[1]);
                case SECOND:
                    return Integer.parseInt(timeArr[2]);
                default:
                    return -1;
            }
        }
    }
}
