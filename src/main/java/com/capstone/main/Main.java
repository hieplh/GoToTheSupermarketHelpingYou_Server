package com.capstone.main;

import com.capstone.controller.OrderController;
import static com.capstone.controller.OrderController.listOrderInProcess;
import static com.capstone.controller.OrderController.mapOrderInQueue;
import com.capstone.helper.DateTimeHelper;
import com.capstone.mall.Mall;
import com.capstone.order.Order;
import com.capstone.order.OrderDetail;
import com.capstone.order.TimeTravel;
import com.capstone.utils.DBUtils;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("com.capstone.controller")
@ComponentScan("com.capstone.schedule")
@EnableScheduling
public class Main extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        new Main().loadMapOrderInQueue();
        for (Map.Entry<Order, Integer> entry : mapOrderInQueue.entrySet()) {
            System.out.println(entry.getValue());
        }
    }

    public void loadMapOrderInQueue() {
        try {
            List<Order> listOrders = loadOrder();
            loadOrderDetail(listOrders);

            if (OrderController.mapOrderInQueue == null) {
                OrderController.mapOrderInQueue = new HashMap<>();
            }
            for (Order order : listOrders) {
                OrderController.mapOrderInQueue.put(order, new DateTimeHelper().calculateTimeForShipper(order, order.getTimeTravel()));
                order.setTimeTravel(null);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    private List<Order> loadOrder() throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Order> listOrders = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = "EXEC GET_ORDERS_INQUEUE_BY_DATE ?";
                stmt = con.prepareStatement(sql);
                stmt.setDate(1, new Date(Calendar.getInstance(
                        TimeZone.getTimeZone("Asia/Ho_Chi_Minh"), Locale.forLanguageTag("vi-vn"))
                        .getTimeInMillis()));

                rs = stmt.executeQuery();
                while (rs.next()) {
                    if (OrderController.mapOrderInQueue == null) {
                        OrderController.mapOrderInQueue = new HashMap<>();
                    }
                    if (listOrders == null) {
                        listOrders = new ArrayList<>();
                    }
                    listOrders.add(new Order(rs.getString("ID"),
                            rs.getString("CUST"),
                            new Mall(null,
                                    rs.getString("NAME"),
                                    rs.getString("ADDR_1"),
                                    rs.getString("ADDR_2"),
                                    rs.getString("ADDR_3"),
                                    rs.getString("ADDR_4"),
                                    rs.getString("LAT"),
                                    rs.getString("LNG")),
                            rs.getString("NOTE"),
                            rs.getDouble("COST_SHOPPING"),
                            rs.getDouble("COST_DELIVERY"),
                            rs.getDouble("TOTAL_COST"),
                            rs.getDate("DATE_DELIVERY"),
                            rs.getTime("TIME_DELIVERY"),
                            new TimeTravel(rs.getTime("GOING"),
                                    rs.getTime("SHOPPING"),
                                    rs.getTime("DELIVERY"),
                                    rs.getTime("TRAFFIC"))));
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return listOrders;
    }

    private void loadOrderDetail(List<Order> listOrders) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future<?>> futures = new ArrayList<>();
        futures.add(executorService.submit(new CallableImp<>(listOrders.subList(0, listOrders.size() / 2))));
        futures.add(executorService.submit(new CallableImp<>(listOrders.subList(listOrders.size() / 2, listOrders.size()))));

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getMessage());
            }
        }
        executorService.shutdown();
    }

    class CallableImp<T> implements Callable<T> {

        private final List<Order> list;

        public CallableImp(List<Order> list) {
            this.list = list;
        }

        @Override
        public T call() throws Exception {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "EXEC GET_ORDER_DETAIL_BY_ID ?";
                    stmt = con.prepareStatement(sql);
                    for (Order order : list) {
                        stmt.setString(1, order.getId());
                        rs = stmt.executeQuery();
                        while (rs.next()) {
                            order.getDetails().add(new OrderDetail(rs.getString("ID"),
                                    rs.getString("NAME"),
                                    rs.getString("IMAGE"),
                                    rs.getDouble("ORIGINAL_PRICE"),
                                    rs.getDouble("PAID_PRICE"),
                                    rs.getDouble("WEIGHT"),
                                    rs.getInt("SALE_OFF")));
                        }
                    }
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            }
            return null;
        }
    }
}
