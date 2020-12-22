package com.smhu.iface;

import java.sql.SQLException;

public interface ITransaction {

    public int updateRechargeTransaction(String authorId, double amount) throws ClassNotFoundException, SQLException;

    public int updateDeliveryTransaction(String affectedId, String authorId, double amount, int status, String orderId) throws ClassNotFoundException, SQLException;

    public int updateRefundTransaction(String affectedId, String authorId, double amount, int status, String orderId) throws ClassNotFoundException, SQLException;
}
