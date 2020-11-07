package com.smhu.iface;

import com.smhu.account.Shipper;

public interface IShipper {

//    public void addShipperAccountAvailable(Shipper shipper);
//    
//    public void addShipperAccountInProgress(Shipper shipper);
    
    public void addShipper(Shipper shipper);

    public Shipper getShipper(String id);
//    public Shipper getAvailableShipperAccount(String id);
//    
//    public Shipper getInProgressShipperAccount(String id);

    public void changeStatusOfShipper(String shipperId);
}
