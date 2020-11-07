package com.smhu.comparator;

import com.smhu.account.Shipper;
import java.util.Comparator;

public class SortByHighActive implements Comparator<Shipper> {

    @Override
    public int compare(Shipper o1, Shipper o2) {
        int sumO1 = o1.getNumDelivery() + o1.getNumCancel() > 0 ? o1.getNumDelivery() + o1.getNumCancel() : 1;
        int sumO2 = o2.getNumDelivery() + o2.getNumCancel() > 0 ? o2.getNumDelivery() + o2.getNumCancel() : 1;
        if (Math.round((double) (o1.getNumDelivery() / sumO1 * 1000)) / 1000.0
                == Math.round((double) (o2.getNumDelivery() / sumO2 * 1000)) / 1000.0) {
            if (o1.getNumDelivery() > o2.getNumDelivery()) {
                return -1;
            } else {
                return 1;
            }
        } else if (Math.round((double) (o1.getNumDelivery() / sumO1 * 1000)) / 1000.0
                > Math.round((double) (o2.getNumDelivery() / sumO2 * 1000)) / 1000.0) {
            return -1;
        } else {
            return 1;
        }
    }
}
