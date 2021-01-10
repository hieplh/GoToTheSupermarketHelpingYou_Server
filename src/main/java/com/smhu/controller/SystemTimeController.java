package com.smhu.controller;

import com.smhu.iface.IModeration;
import com.smhu.system.SystemTime;
import com.smhu.web.ModerationController;
import java.sql.Date;
import java.sql.Time;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@CrossOrigin
@RequestMapping("/api")
public class SystemTimeController {

    final String DATE_TIME = "datetime";
    final String DATE = "date";
    final String TIME = "time";
    final String MILISECOND = "milisecond";

    SystemTimeService service;

    @GetMapping("/system/format")
    public ResponseEntity getAllTypeSystemTime() {
        Map<String, String> map = new HashMap<>();
        map.put(DATE_TIME, "Date and Time. Format: yyyy-MM-dd hh:mm:ss");
        map.put(DATE, "Date. Format: yyyy-MM-dd");
        map.put(TIME, "Time. Format: hh:mm:ss");
        map.put(MILISECOND, "Milisecond");
        return new ResponseEntity(map, HttpStatus.OK);
    }

    @GetMapping("/system/time/{type}")
    public ResponseEntity getSystemTime(@PathVariable("type") String type) {
        switch (type.toLowerCase()) {
            case DATE_TIME:
                return new ResponseEntity(new Date(SystemTime.SYSTEM_TIME) + " " + new Time(SystemTime.SYSTEM_TIME), HttpStatus.OK);
            case DATE:
                return new ResponseEntity(new Date(SystemTime.SYSTEM_TIME), HttpStatus.OK);
            case TIME:
                return new ResponseEntity(new Time(SystemTime.SYSTEM_TIME), HttpStatus.OK);
            case MILISECOND:
                return new ResponseEntity(SystemTime.SYSTEM_TIME, HttpStatus.OK);
            default:
                return new ResponseEntity(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @GetMapping("/system/change/{time}")
    public ResponseEntity updateSystemTime(@PathVariable("time") String time) {
        service = new SystemTimeService();
        ZonedDateTime dateTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(dateTime.getYear(), dateTime.getMonthValue() - 1, dateTime.getDayOfMonth(),
                service.getTime(time, service.HOUR),
                service.getTime(time, service.MINUTE),
                service.getTime(time, service.SECOND));

        SystemTime.SYSTEM_TIME = calendar.getTimeInMillis();

        IModeration modListener = new ModerationController();
        modListener.reloadCommission();
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
