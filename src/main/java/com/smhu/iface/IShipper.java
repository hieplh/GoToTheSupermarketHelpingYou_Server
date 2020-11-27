package com.smhu.iface;

import com.smhu.account.Shipper;
import java.sql.SQLException;

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
    
    public int updateNumDeliveryOfShipper(String shipperId, int num) throws ClassNotFoundException, SQLException;
    
    public int updateNumCancelOfShipper(String shipperId, int num) throws ClassNotFoundException, SQLException;
    
    public void checkWalletShipperAccount(Shipper shipper);
}
