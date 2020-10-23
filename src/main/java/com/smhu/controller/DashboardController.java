package com.smhu.controller;

import com.smhu.iface.IStatus;
import com.smhu.response.ResponseMsg;
import com.smhu.utils.DBUtils;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@RequestMapping("/api")
public class DashboardController {

    DashboardService service;

    @GetMapping("/dashboard/{type}/count")
    public ResponseEntity<?> getCountOrders(@PathVariable("type") String type) {
        try {
            service = new DashboardService();
            return new ResponseEntity<>(service.getOrderHistoriesCount(type), HttpStatus.OK);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(HistoryController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.OK);
        }
    }

    class DashboardService {

        final String ALL = "all";
        final String INQUEUE = "inqueue";
        final String INPROGRESS = "inprogress";
        final String UPCOMING = "upcoming";
        final String DONE = "done";
        final String CANCEL = "cancel";

        int getOrderHistoriesCount(String type) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            IStatus statusListener = new StatusController().getStatusListener();
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"), new Locale("vi", "vn"));

            try {
                con = DBUtils.getConnection();
                if (con != null) {

                    StringBuilder sql = new StringBuilder();
                    sql.append("SELECT COUNT(*) AS COUNT\n"
                            + "FROM ORDERS O")
                            .append("\n");
                    sql.append("WHERE DATE_DELIVERY = ?")
                            .append("\n");
                    switch (type.toLowerCase()) {
                        case INQUEUE:
                            sql.append("AND STATUS = " + statusListener.getStatusIsPaidOrder());
                            break;
                        case INPROGRESS:
                            sql.append("AND STATUS >= " + statusListener.getStatusIsAccept()
                                    + " AND STATUS <= " + (statusListener.getStatusIsDoneOrder() - 2));
                            break;
                        case UPCOMING:
                            sql.append("AND STATUS = " + (statusListener.getStatusIsDoneOrder() - 1));
                            break;
                        case DONE:
                            sql.append("AND STATUS = " + statusListener.getStatusIsDoneOrder());
                            break;
                        default:
                            break;
                    }
                    stmt = con.prepareStatement(sql.toString());
                    stmt.setDate(1, new Date(cal.getTimeInMillis()));
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        return rs.getInt("COUNT");
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
            return 0;
        }
    }
}
