package com.smhu.callable;

import com.smhu.dao.TransactionDAO;
import com.smhu.iface.ITransaction;
import com.smhu.statistic.Statistic;
import java.util.Date;
import java.util.concurrent.Callable;

public class NumberOfOrders<T> implements Callable<T> {

    private String accountId;
    private Date date;
    private Statistic statistic;

    public NumberOfOrders() {
    }

    public NumberOfOrders(String accountId, Date date, Statistic statistic) {
        this.accountId = accountId;
        this.date = date;
        this.statistic = statistic;
    }

    @Override
    public T call() throws Exception {
        ITransaction trans = new TransactionDAO();
        statistic.setNumberOfOrders(trans.getNumOfOrders(accountId, date));
        return (T) ((Boolean) true);
    }
}
