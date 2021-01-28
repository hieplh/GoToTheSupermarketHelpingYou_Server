package com.smhu.callable;

import com.smhu.dao.TransactionDAO;
import com.smhu.iface.ITransaction;
import com.smhu.statistic.Statistic;
import com.smhu.statistic.Week;
import com.smhu.transaction.Transaction;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class WeeksStatistic<T> implements Callable<T> {

    private String accountId;
    private Date date;
    private Statistic statistic;

    public WeeksStatistic() {
    }

    public WeeksStatistic(String accountId, Date date, Statistic statistic) {
        this.accountId = accountId;
        this.date = date;
        this.statistic = statistic;
    }

    @Override
    public T call() throws Exception {
        statistic.setWeeks(initWeek());
        ITransaction trans = new TransactionDAO();
        List<Transaction> result = trans.getAmountDetailStatisticOfShipper(accountId, date);
        if (result != null) {
            setAmountDetail(result);
        }
        return (T) ((Boolean) true);
    }

    private List<Week> initWeek() {
        Calendar cal = Calendar.getInstance();
        List<Week> list = new ArrayList<>();
        cal.setTime(date);
        int theLastDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int index = 0;
        for (int i = 0; i < theLastDayOfMonth; i++) {
            if (index + 1 <= (i + 1) / 7.0 || (i + 1) == theLastDayOfMonth) {
                Week week = new Week();
                week.setWeek(String.valueOf(index + 1));

                String s;
                if ((i + 1) == theLastDayOfMonth) {
                    s = String.valueOf(1 + index * 7) + "-" + String.valueOf(theLastDayOfMonth);
                } else {
                    s = String.valueOf(1 + index * 7) + "-" + String.valueOf((index + 1) * 7);
                }
                week.setFromdate(s);
                list.add(week);
                index++;
            }
        }
        return list;
    }

    private void setAmountDetail(List<Transaction> result) {
        Calendar cal = Calendar.getInstance();
        int index = 0;
        for (Transaction trans : result) {
            cal.setTimeInMillis(trans.getCreateDate().getTime());
            int day = cal.get(Calendar.DAY_OF_MONTH);

            if (day % 7 == 0) {
                index = (day / 7) - 1;
            } else {
                index = day / 7;
            }

            Week week = statistic.getWeeks().get(index);
            if (trans.getStatus() > 0) {
                week.setAmountEarned(week.getAmountEarned() + trans.getAmount());
            } else {
                week.setAmountCharged(week.getAmountCharged() + trans.getAmount());
            }
        }
    }
}
