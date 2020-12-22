package com.smhu.dao;

import static com.smhu.controller.ShipperController.mapAvailableShipper;
import static com.smhu.controller.ShipperController.mapInProgressShipper;

import com.smhu.account.Shipper;
import com.smhu.iface.IShipper;

public class ShipperDAO extends AccountDAO implements IShipper {

    @Override
    public Shipper getShipper(String id) {
        Shipper shipper = getAvailableShipperAccount(id);
        if (shipper == null) {
            shipper = getInProgressShipperAccount(id);
            if (shipper == null) {
                return null;
            }
        }
        return shipper;
    }

    @Override
    public void addShipper(Shipper shipper) {
        if (mapInProgressShipper.containsKey(shipper.getUsername())) {
            return;
        }
        mapAvailableShipper.put(shipper.getUsername(), shipper);
    }

    @Override
    public void changeStatusOfShipper(String shipperId) {
        if (mapAvailableShipper.containsKey(shipperId)) {
            Shipper shipper = mapAvailableShipper.remove(shipperId);
            mapInProgressShipper.put(shipperId, shipper);
        } else {
            Shipper shipper = mapInProgressShipper.remove(shipperId);
            mapAvailableShipper.put(shipperId, shipper);
        }
    }

    @Override
    public void checkWalletShipperAccount(Shipper shipper) {
        if (shipper.getWallet() >= 0) {
            return;
        }
        removeShipper(shipper.getUsername());
    }

    Shipper getAvailableShipperAccount(String id) {
        return mapAvailableShipper.getOrDefault(id, null);
    }

    Shipper getInProgressShipperAccount(String id) {
        return mapInProgressShipper.getOrDefault(id, null);
    }

    @Override
    public boolean removeShipper(String id) {
        if (mapAvailableShipper.containsKey(id)) {
            mapAvailableShipper.remove(id);
        } else {
            mapInProgressShipper.remove(id);
        }
        return true;
    }
}
