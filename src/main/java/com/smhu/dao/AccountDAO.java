package com.smhu.dao;

import com.smhu.account.Account;
import com.smhu.account.Address;
import com.smhu.account.Customer;
import com.smhu.account.Shipper;
import com.smhu.controller.AccountController;
import com.smhu.iface.IAccount;
import com.smhu.utils.DBUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountDAO implements IAccount {

    public final String CUSTOMER = "CUSTOMER";
    public final String SHIPPER = "SHIPPER";
    public final String STAFF = "STAFF";

    public final String EMAIL = "EMAIL";
    public final String PHONE = "PHONE";

    final int ROWS = 20;

    private int convertPageToIndex(String page) {
        int tmp = Integer.parseInt(page);
        return tmp > 0 ? (tmp - 1) * ROWS : 0;
    }

    @Override
    public String getEncryptionPassword(String username, String type) throws SQLException, ClassNotFoundException {
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

    @Override
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

    @Override
    public Object getAccountByUsername(String username, String password, String type) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = "EXEC GET_ACCOUNT_BY_USERNAME ?, ?, ?";
                if (type.toUpperCase().equals(SHIPPER)) {
                    sql = "EXEC GET_ACCOUNT_SHIPPER_BY_USERNAME ?, ?, ?";
                }
                stmt = con.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setString(3, type);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    if (type.toUpperCase().equals(SHIPPER)) {
                        return new Shipper(new Account(rs.getString("ID"),
                                rs.getString("USERNAME"),
                                rs.getString("FIRST_NAME"),
                                rs.getString("MID_NAME"),
                                rs.getString("LAST_NAME"),
                                rs.getString("EMAIL"),
                                rs.getString("PHONE"),
                                rs.getDate("DOB"),
                                rs.getString("ROLE")),
                                rs.getInt("NUM_SUCCESS"),
                                rs.getInt("NUM_CANCEL"),
                                rs.getInt("MAX_ORDER"),
                                rs.getDouble("WALLET"),
                                rs.getDouble("RATING"),
                                null, null, null);
                    }
                    return new Customer(new Account(rs.getString("ID"),
                            rs.getString("USERNAME"),
                            rs.getString("FIRST_NAME"),
                            rs.getString("MID_NAME"),
                            rs.getString("LAST_NAME"),
                            rs.getString("EMAIL"),
                            rs.getString("PHONE"),
                            rs.getDate("DOB"),
                            rs.getString("ROLE")),
                            rs.getInt("NUM_SUCCESS"),
                            rs.getInt("NUM_CANCEL"),
                            rs.getDouble("WALLET"),
                            null);
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

    @Override
    public int getCountAccounts(String role) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = "SELECT COUNT(*)\n"
                        + "FROM ACCOUNT\n"
                        + "WHERE ROLE = ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, role);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1);
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

    @Override
    public int getCountAccounts(String role, String search) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = "SELECT COUNT(*)\n"
                        + "FROM ACCOUNT\n"
                        + "WHERE ROLE = ? AND (ID LIKE ? OR EMAIL LIKE ? OR PHONE LIKE ?)";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, role);
                stmt.setString(2, "%" + search + "%");
                stmt.setString(3, "%" + search + "%");
                stmt.setString(4, "%" + search + "%");
                rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1);
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

    @Override
    public List<Address> getAddressOfAccount(String accountId) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Address> list = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = "SELECT *\n"
                        + "FROM GET_ADDRESS_OF_ACCOUNT_BY_ID\n"
                        + "WHERE ACCOUNT = ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, accountId);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(new Address(
                            rs.getString("ADDR_1"),
                            rs.getString("ADDR_2"),
                            rs.getString("ADDR_3"),
                            rs.getString("ADDR_4")));
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

    @Override
    public Object getAccountById(String id, String role) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = "EXEC GET_ACCOUNT_BY_ID ?, ?";
                if (role.toUpperCase().equals(SHIPPER)) {
                    sql = "EXEC GET_ACCOUNT_SHIPPER_BY_ID ?, ?";
                }
                stmt = con.prepareStatement(sql);
                stmt.setString(1, id);
                stmt.setString(2, role);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    switch (role.toUpperCase()) {
                        case SHIPPER:
                            return new Shipper(new Account(rs.getString("ID"),
                                    rs.getString("USERNAME"),
                                    rs.getString("FIRST_NAME"),
                                    rs.getString("MID_NAME"),
                                    rs.getString("LAST_NAME"),
                                    rs.getString("EMAIL"),
                                    rs.getString("PHONE"),
                                    rs.getDate("DOB"),
                                    rs.getString("ROLE")),
                                    rs.getInt("NUM_SUCCESS"),
                                    rs.getInt("NUM_CANCEL"),
                                    rs.getInt("MAX_ORDER"),
                                    rs.getDouble("WALLET"),
                                    rs.getDouble("RATING"),
                                    null, null, null);
                        case CUSTOMER:
                            return new Customer(new Account(rs.getString("ID"),
                                    rs.getString("USERNAME"),
                                    rs.getString("FIRST_NAME"),
                                    rs.getString("MID_NAME"),
                                    rs.getString("LAST_NAME"),
                                    rs.getString("EMAIL"),
                                    rs.getString("PHONE"),
                                    rs.getDate("DOB"),
                                    rs.getString("ROLE")),
                                    rs.getInt("NUM_SUCCESS"),
                                    rs.getInt("NUM_CANCEL"),
                                    rs.getDouble("WALLET"),
                                    null);
                        default:
                            return new Account(rs.getString("ID"),
                                    rs.getString("USERNAME"),
                                    rs.getString("FIRST_NAME"),
                                    rs.getString("MID_NAME"),
                                    rs.getString("LAST_NAME"),
                                    rs.getString("EMAIL"),
                                    rs.getString("PHONE"),
                                    rs.getDate("DOB"),
                                    rs.getString("ROLE"));
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

    @Override
    public List<? super Account> getAccountBySearch(String role, String search, String page) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<? super Account> list = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = "EXEC GET_ACCOUNT_BY_SEARCH ?, ?, ?, ?";
                if (role.toUpperCase().equals(SHIPPER)) {
                    sql = "EXEC GET_ACCOUNT_SHIPPER_BY_SEARCH ?, ?, ?, ?";
                }
                stmt = con.prepareStatement(sql);
                stmt.setString(1, role);
                stmt.setString(2, "%" + search + "%");
                stmt.setInt(3, convertPageToIndex(page));
                stmt.setInt(4, ROWS);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    switch (role.toUpperCase()) {
                        case SHIPPER:
                            list.add(new Shipper(new Account(rs.getString("ID"),
                                    rs.getString("USERNAME"),
                                    rs.getString("FIRST_NAME"),
                                    rs.getString("MID_NAME"),
                                    rs.getString("LAST_NAME"),
                                    rs.getString("EMAIL"),
                                    rs.getString("PHONE"),
                                    rs.getDate("DOB"),
                                    rs.getString("ROLE")),
                                    rs.getInt("NUM_SUCCESS"),
                                    rs.getInt("NUM_CANCEL"),
                                    rs.getInt("MAX_ORDER"),
                                    rs.getDouble("WALLET"),
                                    rs.getDouble("RATING"),
                                    null, null, null));
                            break;
                        case CUSTOMER:
                            list.add(new Customer(new Account(rs.getString("ID"),
                                    rs.getString("USERNAME"),
                                    rs.getString("FIRST_NAME"),
                                    rs.getString("MID_NAME"),
                                    rs.getString("LAST_NAME"),
                                    rs.getString("EMAIL"),
                                    rs.getString("PHONE"),
                                    rs.getDate("DOB"),
                                    rs.getString("ROLE")),
                                    rs.getInt("NUM_SUCCESS"),
                                    rs.getInt("NUM_CANCEL"),
                                    rs.getDouble("WALLET"),
                                    null));
                            break;
                        default:
                            return null;
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
        return list;
    }

    @Override
    public List<? super Account> getAccounts(String type, String page) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<? super Account> list = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = "EXEC GET_ALL_ACCOUNT_BY_SEARCH ?, ?, ?";
                if (type.toUpperCase().equals(SHIPPER)) {
                    sql = "EXEC GET_ALL_ACCOUNT_SHIPPER_BY_SEARCH ?, ?, ?";
                }
                stmt = con.prepareStatement(sql);
                stmt.setString(1, type);
                stmt.setInt(2, convertPageToIndex(page));
                stmt.setInt(3, ROWS);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    if (type.toUpperCase().equals(SHIPPER)) {
                        list.add(new Shipper(new Account(rs.getString("ID"),
                                rs.getString("USERNAME"),
                                rs.getString("FIRST_NAME"),
                                rs.getString("MID_NAME"),
                                rs.getString("LAST_NAME"),
                                rs.getString("EMAIL"),
                                rs.getString("PHONE"),
                                rs.getDate("DOB"),
                                rs.getString("ROLE")),
                                rs.getInt("NUM_SUCCESS"),
                                rs.getInt("NUM_CANCEL"),
                                rs.getInt("MAX_ORDER"),
                                rs.getDouble("WALLET"),
                                rs.getDouble("RATING"),
                                null, null, null));
                    } else if (type.toUpperCase().equals(CUSTOMER)) {
                        list.add(new Customer(new Account(rs.getString("ID"),
                                rs.getString("USERNAME"),
                                rs.getString("FIRST_NAME"),
                                rs.getString("MID_NAME"),
                                rs.getString("LAST_NAME"),
                                rs.getString("EMAIL"),
                                rs.getString("PHONE"),
                                rs.getDate("DOB"),
                                rs.getString("ROLE")),
                                rs.getInt("NUM_SUCCESS"),
                                rs.getInt("NUM_CANCEL"),
                                rs.getDouble("WALLET"),
                                null));
                    } else {
                        list.add(new Account(rs.getString("ID"),
                                rs.getString("USERNAME"),
                                rs.getString("FIRST_NAME"),
                                rs.getString("MID_NAME"),
                                rs.getString("LAST_NAME"),
                                rs.getString("EMAIL"),
                                rs.getString("PHONE"),
                                rs.getDate("DOB"),
                                rs.getString("ROLE")));
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
        return list;
    }

    @Override
    public int deleteAccount(String accountId, boolean flag) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = "UPDATE ACCOUNT SET IS_ACTIVE = ?\n"
                        + "WHERE ID = ?";
                stmt = con.prepareStatement(sql);
                stmt.setBoolean(1, flag);
                stmt.setString(2, accountId);
                return stmt.executeUpdate();
            }
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return 0;
    }

    @Override
    public int updateWalletAccount(String accountId, double amount) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = "EXEC UPDATE_WALLET_ACCOUNT ?, ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, accountId);
                stmt.setDouble(2, amount);
                return stmt.executeUpdate();
            }
        } finally {
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
