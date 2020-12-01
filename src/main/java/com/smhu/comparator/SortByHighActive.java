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
public class SortByHighActive implements Comparator<String> {

    @Override
    public int compare(String s1, String s2) {
        Shipper shipper1 = ShipperController.mapAvailableShipper.get(s1);
        if (shipper1 == null) {
            return 1;
        }
        Shipper shipper2 = ShipperController.mapAvailableShipper.get(s2);
        if (shipper2 == null) {
            return -1;
        }

        double rating1 = shipper1.getRating();
        double rating2 = shipper2.getRating();
        int sumO1 = shipper1.getNumDelivery() + shipper1.getNumCancel() > 0 ? shipper1.getNumDelivery() + shipper1.getNumCancel() : 1;
        int sumO2 = shipper2.getNumDelivery() + shipper2.getNumCancel() > 0 ? shipper2.getNumDelivery() + shipper2.getNumCancel() : 1;
        double rateDelivery1 = Math.round((double) (shipper1.getNumDelivery() / sumO1 * 1000)) / 1000.0;
        double rateDelivery2 = Math.round((double) (shipper2.getNumDelivery() / sumO2 * 1000)) / 1000.0;

        if (rateDelivery1 >= rateDelivery2 && rating1 > rating2) {
            return -1;
        }
        if (rateDelivery1 >= rateDelivery2 && rating1 == rating2) {
            return -1;
        }
        if (rating1 < rating2) {
            if (rating1 >= 4 && shipper1.getNumDelivery() >= shipper2.getNumDelivery() * 10) {
                return -1;
            }
        }
        return 1;
    }
}
