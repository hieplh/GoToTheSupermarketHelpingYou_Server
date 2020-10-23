package com.smhu.controller;

import com.smhu.iface.IStatus;
import java.util.HashMap;
import java.util.Map;

public class StatusController {

    public static Map<Integer, String> mapStatus = new HashMap<>();
    IStatus statusListener = new StatusService();

    public IStatus getStatusListener() {
        return statusListener;
    }

    class StatusService implements IStatus {

        @Override
        public int getStatusIsDoneOrder() {
            return StatusController.mapStatus.keySet()
                    .stream()
                    .filter(t -> String.valueOf(t).matches("2\\d"))
                    .mapToInt(value -> value)
                    .max()
                    .getAsInt();
        }

        @Override
        public int getStatusIsAccept() {
            return StatusController.mapStatus.keySet()
                    .stream()
                    .filter(t -> String.valueOf(t).matches("2\\d"))
                    .sorted()
                    .findFirst()
                    .orElse(21);
        }

        @Override
        public int getStatusIsPaidOrder() {
            return StatusController.mapStatus.keySet()
                    .stream()
                    .filter((s) -> {
                        return s.toString().matches("1\\d");
                    })
                    .mapToInt((value) -> {
                        return value;
                    }).max().getAsInt();
        }

    }
}
