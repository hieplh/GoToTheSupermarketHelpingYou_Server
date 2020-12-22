package com.smhu.iface;

import com.smhu.account.Shipper;
import java.sql.SQLException;

public interface IShipper {

    public void addShipper(Shipper shipper);

    public Shipper getShipper(String id);
    
    public boolean removeShipper(String id);

    public void changeStatusOfShipper(String shipperId);

    public void checkWalletShipperAccount(Shipper shipper);

    public int updateNumSuccess(String shipperId, int num) throws ClassNotFoundException, SQLException;

    public int updateNumCancel(String shipperId, int num) throws ClassNotFoundException, SQLException;
}
