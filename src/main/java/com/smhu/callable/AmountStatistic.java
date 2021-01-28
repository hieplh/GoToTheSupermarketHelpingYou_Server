package com.smhu.callable;

import com.smhu.dao.TransactionDAO;
import com.smhu.iface.ITransaction;
import com.smhu.statistic.Statistic;
import java.util.Date;
import java.util.concurrent.Callable;

public class AmountStatistic<T> implements Callable<T> {

    private String accountId;
    private Date date;
    private Statistic statistic;

    public AmountStatistic() {
    }

    public AmountStatistic(String accountId, Date date, Statistic statistic) {
        this.accountId = accountId;
        this.date = date;
        this.statistic = statistic;
    }

    @Override
    public T call() throws Exception {
        ITransaction trans = new TransactionDAO();
        statistic.setAmount(trans.getAmountStatisticOfShipper(accountId, date));
        return (T) ((Boolean) true);
    }
}
