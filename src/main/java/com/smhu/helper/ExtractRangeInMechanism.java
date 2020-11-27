package com.smhu.helper;

import static com.smhu.controller.ShipperController.mapMechanismReleaseOrder;
import java.util.TreeMap;

public class ExtractRangeInMechanism {

    public static int getTheShortesRangeInMechanism() {
        TreeMap<Integer, Integer> sortMechanismReleaseOrder = new TreeMap<>(mapMechanismReleaseOrder);
        return sortMechanismReleaseOrder.get(sortMechanismReleaseOrder.firstKey());
    }

    public static int getTheLongestRangeInMechanism() {
        TreeMap<Integer, Integer> sortMechanismReleaseOrder = new TreeMap<>(mapMechanismReleaseOrder);
        return sortMechanismReleaseOrder.get(sortMechanismReleaseOrder.lastKey());
    }
}
