package com.smhu.controller;

import com.smhu.callable.AmountStatistic;
import com.smhu.callable.NumberOfOrders;
import com.smhu.callable.WeeksStatistic;
import com.smhu.dao.TransactionDAO;
import com.smhu.iface.ITransaction;
import com.smhu.response.ResponseMsg;
import com.smhu.statistic.Statistic;
import com.smhu.system.SystemTime;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class TransactionController {

    private final ITransaction service;

    public TransactionController() {
        this.service = new TransactionDAO();
    }

    @GetMapping("/trans/{accountId}")
    public ResponseEntity<?> getAllTransactionByAccountId(@PathVariable("accountId") String authorId) {
        try {
            return new ResponseEntity<>(service.getTransaction(authorId), HttpStatus.OK);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(TransactionController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/trans/{accountId}/{monthly}")
    public ResponseEntity<?> getMonthlyStatistic(@PathVariable("accountId") String accountId, @PathVariable("monthly") String monthly) {
        if (checkIsNullOrEmpty(accountId) && checkIsNullOrEmpty(monthly)) {
            return new ResponseEntity("Parameters must not be blank", HttpStatus.METHOD_NOT_ALLOWED);
        }
        if (!checkFormatMonthly(monthly)) {
            return new ResponseEntity("Wrong format month. Format: yyyy-MM", HttpStatus.METHOD_NOT_ALLOWED);
        }

        if (!checkMonth(monthly)) {
            return new ResponseEntity("Month is not correct. Month: 1-12", HttpStatus.METHOD_NOT_ALLOWED);
        }

        Date date = parseMonthYear(monthly);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Statistic statistic = new Statistic();
        statistic.setOfMonthYear(cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1));
        statistic.setFromDate("1" +  "-" + cal.get(Calendar.DAY_OF_MONTH));

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<Future<?>> futures = new ArrayList<>();
        futures.add(executorService.submit(new NumberOfOrders(accountId, date, statistic)));
        futures.add(executorService.submit(new AmountStatistic(accountId, date, statistic)));
        futures.add(executorService.submit(new WeeksStatistic(accountId, date, statistic)));

        try {
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (ExecutionException | InterruptedException e) {
                    Logger.getLogger(TransactionController.class.getName()).log(Level.SEVERE, "Get Statistic: {0}", e.getMessage());
                }
            }
        } finally {
            executorService.shutdown();
        }
        return new ResponseEntity(statistic, HttpStatus.OK);
    }

    private boolean checkIsNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    private boolean checkFormatMonthly(String monthly) {
        String regex = "([0-1]?\\d(-[1-9]\\d{3})?)|([1-9]\\d{3}-[0-1]?\\d)";
        return monthly.matches(regex);
    }

    private boolean checkMonth(String monthly) {
        String[] arrMonthly = monthly.split("-");
        for (String s : arrMonthly) {
            if (s.length() <= 2) {
                if (Integer.parseInt(s) >= 12) {
                    return false;
                }
            }
        }
        return true;
    }

    private Date parseMonthYear(String monthly) {
        Calendar cal = Calendar.getInstance();
        String[] arrMonthly = monthly.split("-");
        String[] result = new String[2];
        if (arrMonthly.length == 1) {
            cal.setTimeInMillis(SystemTime.SYSTEM_TIME);
            result[0] = String.valueOf(cal.get(Calendar.YEAR));
            result[1] = arrMonthly[0];
        } else {
            if (arrMonthly[0].length() <= 2) {
                result[0] = arrMonthly[1];
                result[1] = arrMonthly[0];
            } else {
                result = arrMonthly;
            }
        }
        cal.set(Integer.parseInt(result[0]), Integer.parseInt(result[1]), 0);
        return new Date(cal.getTimeInMillis());
    }

    public ITransaction getTransactionListener() {
        return service;
    }
}
