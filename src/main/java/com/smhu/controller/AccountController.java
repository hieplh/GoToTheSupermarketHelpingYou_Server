package com.smhu.controller;

import com.smhu.account.Account;
import com.smhu.account.AccountLogin;
import com.smhu.iface.IAccount;
import com.smhu.response.ResponseMsg;
import com.smhu.utils.DBUtils;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
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

    public static Map<String, String> mapRoles = new HashMap<>();

    IAccount accountListener = new AccountService();
    AccountService service = new AccountService();

    @PostMapping("/account/username")
    public ResponseEntity<?> getAccountByUsername(@RequestBody AccountLogin accountObj) {
        Account account = null;

        try {
            if (!mapRoles.containsKey(accountObj.getRole().toLowerCase())) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }

            String encrypt = service.getEncryptionPassword(accountObj.getUsername(), accountObj.getRole().toLowerCase());
            if (encrypt == null) {
                return new ResponseEntity<>(new ResponseMsg("Username or Password is not correct"), HttpStatus.NOT_FOUND);
            }

            String encodePassword = service.encryptSHA(accountObj.getPassword() + encrypt);
            account = service.getAccountByUsername(accountObj.getUsername(), encodePassword, accountObj.getRole());
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

    @GetMapping("/account/phone/{phone}/role/{role}")
    public ResponseEntity<?> getAccountByPhone(@PathVariable("phone") String phone, @PathVariable("role") String role) {
        Account account = null;

        try {
            if (mapRoles.containsKey(role)) {
                account = service.getAccountByEmailOrPhone(phone.toLowerCase(), role, service.PHONE);
            } else {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }

            if (account == null) {
                return new ResponseEntity<>(new ResponseMsg("Phone number is not correct"), HttpStatus.NOT_FOUND);
            }
        } catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping("/account/email/{email}/role/{role}")
    public ResponseEntity<?> getAccountByEmail(@PathVariable("email") String email, @PathVariable("role") String role) {
        Account account = null;

        try {
            if (mapRoles.containsKey(role)) {
                account = service.getAccountByEmailOrPhone(email.toLowerCase(), role, service.EMAIL);
            } else {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }

            if (account == null) {
                return new ResponseEntity<>(new ResponseMsg("Email is not correct"), HttpStatus.NOT_FOUND);
            }
        } catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    public IAccount getAccountListener() {
        return accountListener;
    }

    class AccountService implements IAccount {

        final String EMAIL = "EMAIL";
        final String PHONE = "PHONE";

        @Override
        public Map<String, String> getRoles() throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            Map<String, String> map = new HashMap<>();

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "SELECT *\n"
                            + "FROM GET_ALL_ROLES";
                    stmt = con.prepareStatement(sql);
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        map.put(rs.getString("ID"), rs.getString("DESCRIPTION"));
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
            return map;
        }

        String getEncryptionPassword(String username, String type) throws SQLException, ClassNotFoundException {
            if (!AccountController.mapRoles.containsKey(type)) {
                return null;
            }

            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    StringBuilder sql = new StringBuilder();
                    sql.append("SELECT SALT")
                            .append("\n");
                    sql.append("FROM ACCOUNT")
                            .append("\n");
                    sql.append("WHERE IS_ACTIVE = 1")
                            .append("\n");
                    sql.append("AND USERNAME = ?")
                            .append("\n");
                    sql.append("AND ROLE = ?");

                    stmt = con.prepareStatement(sql.toString());
                    stmt.setString(1, username);
                    stmt.setString(2, type);
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

        String encryptSHA(String source) throws NoSuchAlgorithmException, UnsupportedEncodingException {
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

        Account getAccountByUsername(String username, String password, String type) throws SQLException, ClassNotFoundException {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    String sql = "SELECT *\n"
                            + "FROM GET_ACCOUNT\n"
                            + "WHERE USERNAME = ? AND PASSWORD = ? "
                            + "AND ROLE = ?";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    stmt.setString(3, type);
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        return new Account(rs.getString("ID"),
                                rs.getString("USERNAME"),
                                rs.getString("FIRST_NAME"),
                                rs.getString("MID_NAME"),
                                rs.getString("LAST_NAME"),
                                rs.getString("EMAIL"),
                                rs.getString("PHONE"),
                                rs.getDate("DOB"),
                                rs.getString("ROLE"),
                                rs.getInt("NUM_SUCCESS"),
                                rs.getInt("NUM_CANCEL"),
                                rs.getDouble("WALLET"));
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

        Account getAccountByEmailOrPhone(String param, String role, String type) throws SQLException, ClassNotFoundException {
            if (!AccountController.mapRoles.containsKey(role)) {
                return null;
            }

            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            StringBuilder sql = new StringBuilder();

            try {
                con = DBUtils.getConnection();
                if (con != null) {
                    sql.append("SELECT *")
                            .append("\n");
                    sql.append("FROM GET_ACCOUNT")
                            .append("\n");
                    sql.append("WHERE ROLE = ?")
                            .append("\n");
                    switch (type) {
                        case EMAIL:
                            sql.append("AND EMAIL = ?");
                            break;
                        case PHONE:
                            sql.append("AND PHONE = ?");
                            break;
                        default:
                            return null;
                    }

                    stmt = con.prepareStatement(sql.toString());
                    stmt.setString(1, role);
                    stmt.setString(2, param);
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        return new Account(rs.getString("ID"),
                                rs.getString("USERNAME"),
                                rs.getString("FIRST_NAME"),
                                rs.getString("MID_NAME"),
                                rs.getString("LAST_NAME"),
                                rs.getString("EMAIL"),
                                rs.getString("PHONE"),
                                rs.getDate("DOB"),
                                rs.getString("ROLE"),
                                rs.getInt("NUM_SUCCESS"),
                                rs.getInt("NUM_CANCEL"),
                                rs.getDouble("WALLET"));
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
