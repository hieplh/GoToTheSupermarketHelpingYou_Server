package com.smhu.controller;

import com.smhu.account.Account;
import com.smhu.account.AccountLogin;
import com.smhu.msg.ResponseMsg;
import com.smhu.utils.DBUtils;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AccountController {

    final int EMAIL = 1;
    final int PHONE = 2;

    AccountService service = new AccountService();

    @PostMapping("/account/username")
    public ResponseEntity<?> getAccountByUsername(@RequestBody AccountLogin accountObj) {
        Account account = null;

        try {
            String encrypt = service.getEncryptionPassword(accountObj.getUsername());
            if (encrypt == null) {
                return new ResponseEntity<>(new ResponseMsg("Account is not exist"), HttpStatus.NOT_FOUND);
            }

            String encodePassword = service.encryptSHA(accountObj.getPassword() + encrypt);
            account = service.getAccountByUsername(accountObj.getUsername(), encodePassword);
            if (account == null) {
                return new ResponseEntity<>(new ResponseMsg("Username or Password is not correct"), HttpStatus.NOT_FOUND);
            }

        } catch (SQLException | ClassNotFoundException
                | NoSuchAlgorithmException | UnsupportedEncodingException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping("/account/phone/{phone}")
    public ResponseEntity<?> getAccountByPhone(@PathVariable("phone") String phone) {
        Account account = null;

        try {
            account = service.getAccountByEmailOrPhone(phone, PHONE);
            if (account == null) {
                return new ResponseEntity<>(new ResponseMsg("Phone number is not correct"), HttpStatus.NOT_FOUND);
            }

        } catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(account, HttpStatus.OK);
    }
    
    @GetMapping("/account/email/{email}")
    public ResponseEntity<?> getAccountByEmail(@PathVariable("email") String email) {
        Account account = null;

        try {
            account = service.getAccountByEmailOrPhone(email, EMAIL);
            if (account == null) {
                return new ResponseEntity<>(new ResponseMsg("Email is not correct"), HttpStatus.NOT_FOUND);
            }

        } catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    class AccountService {

        String getEncryptionPassword(String username) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "SELECT SALT\n"
                            + "FROM ACCOUNT\n"
                            + "WHERE USERNAME = ? AND IS_ACTIVE = 1";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, username);
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        return rs.getString("SALT");
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

        public String encryptSHA(String source) throws NoSuchAlgorithmException, UnsupportedEncodingException {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(source.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }

        Account getAccountByUsername(String username, String password) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            Account account = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "SELECT *\n"
                            + "FROM GET_ACCOUNT\n"
                            + "WHERE USERNAME = ? AND PASSWORD = ?";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        account = new Account(rs.getString("ID"),
                                username,
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

        Account getAccountByEmailOrPhone(String param, int type) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            Account account = null;
            StringBuilder sql = new StringBuilder();

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    sql.append("SELECT *");
                    sql.append("\n");
                    sql.append("FROM GET_ACCOUNT");
                    sql.append("\n");

                    switch (type) {
                        case EMAIL:
                            sql.append("WHERE EMAIL = ?");
                            break;
                        case PHONE:
                            sql.append("WHERE PHONE = ?");
                            break;
                        default:
                            return null;
                    }

                    stmt = con.prepareStatement(sql.toString());
                    stmt.setString(1, param);
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        account = new Account(rs.getString("ID"),
                                null,
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
