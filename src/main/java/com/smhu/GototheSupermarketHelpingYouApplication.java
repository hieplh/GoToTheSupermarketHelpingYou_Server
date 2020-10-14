package com.smhu;

import com.smhu.controller.MarketController;
import com.smhu.controller.OrderController;
import com.smhu.controller.StatusController;
import com.smhu.helper.DateTimeHelper;
import com.smhu.iface.IOrder;
import com.smhu.market.Market;
import com.smhu.order.Order;
import com.smhu.order.OrderDetail;
import com.smhu.order.TimeTravel;
import com.smhu.status.Status;
import com.smhu.utils.DBUtils;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("com.smhu.controller")
@ComponentScan("com.smhu.schedule")
@EnableScheduling
public class GototheSupermarketHelpingYouApplication {

    public static void main(String[] args) {
        SpringApplication.run(GototheSupermarketHelpingYouApplication.class, args);
    }

    public void init() {
        try {
            Date date = new Date(Calendar.getInstance(
                    TimeZone.getTimeZone("Asia/Ho_Chi_Minh"), Locale.forLanguageTag("vi-vn"))
                    .getTimeInMillis());

            List<Order> totalOrders = loadOrder(date, "%");
            List<Order> inqueueOrders = loadOrder(date, "12");
            List<Market> markets = loadMarket();
            List<Status> listStatus = loadStatus();

            if (listStatus != null) {
                for (Status status : listStatus) {
                    StatusController.mapStatus.put(status.getCode(), status.getDescription());
                }
            }

            if (markets != null) {
                for (Market market : markets) {
                    MarketController.mapMarket.put(market.getId(), market);
                }
            }

            if (totalOrders != null) {
                for (Order order : totalOrders) {
                    OrderController.mapOrders.put(order.getId(), order);
                }
            }

            if (inqueueOrders != null) {
                loadOrderDetail(inqueueOrders);
                for (Order order : inqueueOrders) {
                    OrderController.mapOrderInQueue.put(order, new DateTimeHelper().calculateTimeForShipper(order, order.getTimeTravel()));
                    order.setTimeTravel(null);
                }
                loadOrderInProcess();
            }
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(GototheSupermarketHelpingYouApplication.class.getName()).log(Level.SEVERE, e.getMessage());
        }

    }

    private List<Status> loadStatus() throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Status> list = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = "SELECT CODE, DESCRIPTION\n"
                        + "FROM STATUS\n"
                        + "ORDER BY CODE ASC";
                stmt = con.prepareStatement(sql);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(new Status(rs.getInt("CODE"), rs.getString("DESCRIPTION")));
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
        return list;
    }
    
    private void loadOrderInProcess() {
        IOrder orderListener = new OrderController().getOrderListener();
        orderListener.checkOrderInqueue();
    }

    private List<Market> loadMarket() throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Market> listMarkets = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = "SELECT ID, NAME, ADDR_1, ADDR_2, ADDR_3, ADDR_4, LAT, LNG\n"
                        + "FROM MALL";
                stmt = con.prepareStatement(sql);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    if (listMarkets == null) {
                        listMarkets = new ArrayList<>();
                    }
                    listMarkets.add(new Market(rs.getString("ID"),
                            rs.getString("NAME"),
                            rs.getString("ADDR_1"),
                            rs.getString("ADDR_2"),
                            rs.getString("ADDR_3"),
                            rs.getString("ADDR_4"),
                            rs.getString("LAT"),
                            rs.getString("LNG")));
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
        return listMarkets;
    }

    private List<Order> loadOrder(Date date, String status) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Order> listOrders = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = "EXEC GET_ORDERS_INQUEUE_BY_DATE ?, ?";
                stmt = con.prepareStatement(sql);
                stmt.setDate(1, date);
                stmt.setString(2, status);

                rs = stmt.executeQuery();
                while (rs.next()) {
                    if (listOrders == null) {
                        listOrders = new ArrayList<>();
                    }
                    String id = rs.getString("ID");
                    System.out.println("ID: " + id);
                    Time time = rs.getTime("TIME_DELIVERY");
                    System.out.println("TIME: " + time);
                    
                    listOrders.add(new Order(rs.getString("ID"),
                            rs.getString("CUST"),
                            rs.getString("MALL"),
                            null,
                            null,
                            null,
                            null,
                            rs.getString("NOTE"),
                            rs.getDouble("COST_SHOPPING"),
                            rs.getDouble("COST_DELIVERY"),
                            rs.getDouble("TOTAL_COST"),
                            rs.getDate("DATE_DELIVERY"),
//                            rs.getTime("TIME_DELIVERY",
//                                    Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"),
//                                            Locale.forLanguageTag("vi-vn"))),
                            rs.getTime("TIME_DELIVERY"),
                            new TimeTravel(rs.getTime("GOING"),
                                    rs.getTime("SHOPPING"),
                                    rs.getTime("DELIVERY"),
                                    rs.getTime("TRAFFIC")),
                            rs.getString("LAT"),
                            rs.getString("LNG"),
                            null));
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

    private void loadOrderDetail(List<Order> listOrders) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = "EXEC GET_ORDER_DETAIL_BY_ID ?";
                stmt = con.prepareStatement(sql);
                for (Order order : listOrders) {
                    stmt.setString(1, order.getId());
                    rs = stmt.executeQuery();
                    List<OrderDetail> listDetails = order.getDetails();
                    while (rs.next()) {
                        if (listDetails == null) {
                            listDetails = new ArrayList<>();
                        }
                        listDetails.add(new OrderDetail(rs.getString("ID"),
                                rs.getString("NAME"),
                                rs.getString("IMAGE"),
                                rs.getDouble("ORIGINAL_PRICE"),
                                rs.getDouble("PAID_PRICE"),
                                rs.getDouble("WEIGHT"),
                                rs.getInt("SALE_OFF")));
                    }
                    order.setDetails(listDetails);
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
    }
}
