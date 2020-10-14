package com.smhu.controller;

import com.smhu.account.Account;
import com.smhu.msg.ResponseMsg;
import com.smhu.utils.DBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AccountController {

    AccountService service = new AccountService();
    
    @GetMapping("/account/{username}/{password}")
    public ResponseEntity<?> getApiAccountByUsername(@PathVariable("username") String username, @PathVariable("password") String password) {
        Account account = null;
        try {
            account = service.getAccountByUsername(username, password);
        } catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    class AccountService {

        Account getAccountByUsername(String username, String password) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            Account account = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "SELECT A.ID, A. A.EMAIL, A.ROLE, A.STATUS, A.WALLET\n"
                            + "A.FIRST_NAME, A.MID_NAME, A.LAST_NAME, A.PHONE, A.DOB\n"
                            + "A.NUM_ORDERED, A.NUM_CANCEL"
                            + "FROM ACCOUNT A\n"
                            + "WHERE A.USERNAME = ? AND A.PASSWORD = ? AND A.IS_ACTIVE = 1";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        return new Account(rs.getString("ID"),
                                rs.getString("FIRST_NAME"),
                                rs.getString("MID_NAME"),
                                rs.getString("LAST_NAME"),
                                rs.getString("EMAIL"),
                                rs.getString("PHONE"),
                                rs.getDate("DOB"),
                                rs.getInt("ROLE"),
                                rs.getInt("NUM_ORDERED"),
                                rs.getInt("NUM_CANCEL"),
                                rs.getDouble("WALLET"),
                                rs.getInt("STATUS"));
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
            return account;
        }
    }
}
