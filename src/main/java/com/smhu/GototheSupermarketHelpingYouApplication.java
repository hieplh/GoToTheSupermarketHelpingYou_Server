package com.smhu;

import com.smhu.controller.AccountController;
import com.smhu.controller.MarketController;
import com.smhu.controller.OrderController;
import com.smhu.controller.ShipperController;
import com.smhu.controller.StatusController;
import com.smhu.helper.PropertiesWithJavaConfig;
import com.smhu.iface.IMain;
import com.smhu.iface.IStatus;
import com.smhu.market.Market;
import com.smhu.order.Order;
import com.smhu.order.OrderDetail;
import com.smhu.status.Status;
import com.smhu.utils.DBUtils;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.TreeSet;
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
@ComponentScan("com.smhu.system")
@ComponentScan("com.smhu.helper")
@EnableScheduling
public class GototheSupermarketHelpingYouApplication {

    final String ORDER_RELEASE_CONFIG_FILE_PATH = "order-release.properties";
    
    MainService service;
    IMain mainListener;
    IStatus statusListener;

    public GototheSupermarketHelpingYouApplication() {
        mainListener = new MainService();
    }

    public IMain getMainListener() {
        return mainListener;
    }

    public static void main(String[] args) {
        SpringApplication.run(GototheSupermarketHelpingYouApplication.class, args);
    }

    public void init() {
        service = new MainService();
        statusListener = new StatusController().getStatusListener();
        try {
            Date date = new Date(Calendar.getInstance(
                    TimeZone.getTimeZone("Asia/Ho_Chi_Minh"), Locale.forLanguageTag("vi-vn"))
                    .getTimeInMillis());

            Map<String, String> roles = service.loadRoles();
            if (!roles.isEmpty()) {
                AccountController.mapRoles = roles;
            }

            List<Status> listStatus = service.loadStatus();
            if (listStatus != null) {
                listStatus.forEach((status) -> {
                    StatusController.mapStatus.put(status.getCode(), status.getDescription());
                });
            }

            List<Market> markets = service.loadMarket();
            if (markets != null) {
                markets.forEach((market) -> {
                    MarketController.mapMarket.put(market.getId(), market);
                });
            }

            List<Order> listOrdersInQueue = service.loadOrder(date, String.valueOf(statusListener.getStatusIsPaidOrder()));
            if (listOrdersInQueue != null) {
                service.loadOrderDetail(listOrdersInQueue);
                listOrdersInQueue.forEach(order
                        -> OrderController.mapOrderInQueue.put(order.getId(), order)
                );
                //service.loadOrderInProcess();
            }

            List<Order> listOrderIsDone = service.loadOrder(date, String.valueOf(statusListener.getStatusIsDoneOrder()));
            if (listOrderIsDone != null) {
                service.loadOrderDetail(listOrderIsDone);
                listOrderIsDone.forEach(order -> OrderController.mapOrderIsDone.put(order.getId(), order));
            }
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(GototheSupermarketHelpingYouApplication.class.getName()).log(Level.SEVERE, e.getMessage());
        }

        try {
            Properties properties = service.loadFileConfig(ORDER_RELEASE_CONFIG_FILE_PATH);
            TreeSet<String> treeSet = new TreeSet<>(properties.stringPropertyNames());
            int number = 0;
            int range = 0;
            int count = 0;
            for (String propertyKey : treeSet) {
                if (++count % 2 == 0) {
                    range = Integer.parseInt(properties.getProperty(propertyKey));
                    ShipperController.mapMechanismReleaseOrder.put(number, range);
                } else {
                    number = Integer.parseInt(properties.getProperty(propertyKey));
                }
            }
        } catch (IOException e) {
            Logger.getLogger(GototheSupermarketHelpingYouApplication.class.getName()).log(Level.SEVERE, e.getMessage());
        }
    }

    class MainService implements IMain {

        public Properties loadFileConfig(String path) throws IOException {
            return new PropertiesWithJavaConfig(path).getProperties();
        }
        
        @Override
        public Map<String, String> loadRoles() throws SQLException, ClassNotFoundException {
            return new AccountController().getAccountListener().getRoles();
        }

        @Override
        public List<Status> loadStatus() throws SQLException, ClassNotFoundException {
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

//        public void loadOrderInProcess() {
//            IOrder orderListener = new OrderController().getOrderListener();
//            try {
//                orderListener.checkOrderInqueue();
//            } catch (Exception e) {
//                Logger.getLogger(GototheSupermarketHelpingYouApplication.class.getName()).log(Level.SEVERE, e.getMessage());
//            }
//        }

        @Override
        public List<Market> loadMarket() throws SQLException, ClassNotFoundException {
            return new MarketController().getMarketListener().getBranchMarkets();
        }

        @Override
        public List<Order> loadOrder(Date date, String status) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            Order order = null;
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
                        order = new Order();
                        order.setId(rs.getString("ID"));
                        order.setCust(rs.getString("CUST"));
                        order.setAddressDelivery(rs.getString("ADDRESS_DELIVERY"));
                        order.setNote(rs.getString("NOTE"));
                        order.setMarket(rs.getString("MARKET"));
                        order.setShipper(rs.getString("SHIPPER"));
                        order.setCreateDate(rs.getDate("CREATED_DATE"));
                        order.setCreateTime(rs.getTime("CREATED_TIME"));
                        order.setLastUpdate(rs.getTime("LAST_UPDATE"));
                        order.setStatus(Integer.parseInt(status));
                        order.setCostShopping(rs.getDouble("COST_SHOPPING"));
                        order.setCostDelivery(rs.getDouble("COST_DELIVERY"));
                        order.setTotalCost(rs.getDouble("TOTAL_COST"));
                        order.setDateDelivery(rs.getDate("DATE_DELIVERY"));
                        order.setTimeDelivery(rs.getTime("TIME_DELIVERY"));
                        listOrders.add(order);
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

        @Override
        public void loadOrderDetail(Order order) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            List<OrderDetail> listDetails = order.getDetails();

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "EXEC GET_ORDER_DETAIL_BY_ID ?";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, order.getId());
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        if (listDetails == null) {
                            listDetails = new ArrayList<>();
                        }
                        listDetails.add(new OrderDetail(rs.getString("ID"),
                                rs.getString("FOOD"),
                                rs.getString("NAME"),
                                rs.getString("IMAGE"),
                                rs.getDouble("ORIGINAL_PRICE"),
                                rs.getDouble("PAID_PRICE"),
                                rs.getDouble("WEIGHT"),
                                rs.getInt("SALE_OFF")));
                    }
                    order.setDetails(listDetails);
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

        @Override
        public void loadOrderDetail(List<Order> listOrders) throws SQLException, ClassNotFoundException {
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
                                    rs.getString("FOOD"),
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
}
