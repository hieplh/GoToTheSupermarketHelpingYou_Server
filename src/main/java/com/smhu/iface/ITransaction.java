package com.smhu.iface;

import com.smhu.statistic.Amount;
import com.smhu.statistic.NumberOfOrders;
import com.smhu.transaction.Transaction;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface ITransaction {

    public List<Transaction> getTransaction(String authorId) throws ClassNotFoundException, SQLException;

    public int updateRechargeTransaction(String authorId, double amount) throws ClassNotFoundException, SQLException;

    public int updateDeliveryTransaction(String affectedId, String authorId, double amount, int status, String orderId) throws ClassNotFoundException, SQLException;

    public int updateRefundTransaction(String affectedId, String authorId, double amount, int status, String orderId) throws ClassNotFoundException, SQLException;

    public NumberOfOrders getNumOfOrders(String accountId, Date date) throws ClassNotFoundException, SQLException;
    
    public Amount getAmountStatisticOfShipper(String accountId, Date date) throws ClassNotFoundException, SQLException;
    
    public List<Transaction> getAmountDetailStatisticOfShipper(String accountId, Date date) throws ClassNotFoundException, SQLException;
}
