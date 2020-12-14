package com.smhu.comparator;

import com.smhu.account.Shipper;
import com.smhu.controller.ShipperController;
import java.util.Comparator;

//public class SortByHighActive implements Comparator<Shipper> {
//
//    @Override
//    public int compare(Shipper o1, Shipper o2) {
//        int sumO1 = o1.getNumDelivery() + o1.getNumCancel() > 0 ? o1.getNumDelivery() + o1.getNumCancel() : 1;
//        int sumO2 = o2.getNumDelivery() + o2.getNumCancel() > 0 ? o2.getNumDelivery() + o2.getNumCancel() : 1;
//        if (Math.round((double) (o1.getNumDelivery() / sumO1 * 1000)) / 1000.0
//                == Math.round((double) (o2.getNumDelivery() / sumO2 * 1000)) / 1000.0) {
//            if (o1.getNumDelivery() > o2.getNumDelivery()) {
//                return -1;
//            } else {
//                return 1;
//            }
//        } else if (Math.round((double) (o1.getNumDelivery() / sumO1 * 1000)) / 1000.0
//                > Math.round((double) (o2.getNumDelivery() / sumO2 * 1000)) / 1000.0) {
//            return -1;
//        } else {
//            return 1;
//        }
//    }
//}
public class SortByHighActive implements Comparator<Shipper> {

    @Override
    public int compare(Shipper s1, Shipper s2) {
        double rating1 = s1.getRating();
        double rating2 = s2.getRating();
        int sumO1 = s1.getNumDelivery() + s1.getNumCancel() > 0 ? s1.getNumDelivery() + s1.getNumCancel() : 1;
        int sumO2 = s2.getNumDelivery() + s2.getNumCancel() > 0 ? s2.getNumDelivery() + s2.getNumCancel() : 1;
        double rateDelivery1 = Math.round((double) (s1.getNumDelivery() / sumO1 * 1000)) / 1000.0;
        double rateDelivery2 = Math.round((double) (s2.getNumDelivery() / sumO2 * 1000)) / 1000.0;

        if (rateDelivery1 >= rateDelivery2 && rating1 > rating2) {
            return -1;
        }
        if (rateDelivery1 >= rateDelivery2 && rating1 == rating2) {
            return -1;
        }
        if (rating1 < rating2) {
            if (rating1 >= 4 && s1.getNumDelivery() >= s2.getNumDelivery() * 10) {
                return -1;
            }
        }
        return 1;
    }
}
