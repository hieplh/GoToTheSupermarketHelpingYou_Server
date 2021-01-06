package com.smhu.dao;

import com.smhu.account.Account;
import com.smhu.account.AccountRegister;
import com.smhu.account.Address;
import com.smhu.account.Customer;
import com.smhu.account.Shipper;
import com.smhu.iface.IAccount;
import com.smhu.statement.QueryStatement;
import com.smhu.utils.DBUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

    private String randomSalt() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            if (random.nextInt(2) % 2 == 0) {
                sb.append((char) (97 + random.nextInt(26)));
            } else {
                sb.append((char) (48 + random.nextInt(10)));
            }
        }
        return sb.toString();
    }

    @Override
    public String getEncryptionPassword(String username) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.selectSalt;
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
    public boolean checkExistedPhoneOrVin(String phone, String vin) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.selectPhoneOrVinAccount;
                stmt = con.prepareStatement(sql);
                stmt.setString(1, phone);
                stmt.setString(2, vin != null ? vin : "");
                rs = stmt.executeQuery();
                if (rs.next()) {
                    return true;
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
        return false;
    }

    @Override
    public Object getAccountByUsername(String username, String password, String type) throws SQLException, ClassNotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.selectAccount;
                if (type.toUpperCase().equals(SHIPPER)) {
                    sql = QueryStatement.selectShipperAccount;
                }
                sql = sql
                        + "WHERE PHONE = ? AND PASSWORD = ?\n"
                        + "AND ROLE = ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setString(3, type);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    if (type.toUpperCase().equals(SHIPPER)) {
                        return new Shipper(new Account(
                                rs.getString("PHONE"),
                                rs.getString("FULLNAME"),
                                rs.getDate("DOB"),
                                rs.getString("ROLE")),
                                rs.getInt("NUM_SUCCESS"),
                                rs.getInt("NUM_CANCEL"),
                                rs.getInt("MAX_ORDER"),
                                rs.getDouble("WALLET"),
                                rs.getDouble("RATING"),
                                null, null, null,
                                rs.getString("VIN"));
                    }
                    return new Customer(new Account(
                            rs.getString("PHONE"),
                            rs.getString("FULLNAME"),
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
                String sql = QueryStatement.selectAccountCount
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
                String sql = QueryStatement.selectAccountCount
                        + "WHERE ROLE = ? AND PHONE LIKE ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, role);
                stmt.setString(2, "%" + search + "%");
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
                String sql = QueryStatement.selectAccountAddress
                        + "WHERE ACCOUNT = ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, accountId);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(new Address(
                            rs.getString("STREET"),
                            rs.getString("WARD"),
                            rs.getString("DISTRICT"),
                            rs.getString("PROVINCE")));
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
                String sql = QueryStatement.selectAccount;
                if (role.toUpperCase().equals(SHIPPER)) {
                    sql = QueryStatement.selectShipperAccount;
                }
                sql = sql + "WHERE ROLE = ? AND PHONE = ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, role);
                stmt.setString(2, id);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    switch (role.toUpperCase()) {
                        case SHIPPER:
                            return new Shipper(new Account(
                                    rs.getString("PHONE"),
                                    rs.getString("FULLNAME"),
                                    rs.getDate("DOB"),
                                    rs.getString("ROLE")),
                                    rs.getInt("NUM_SUCCESS"),
                                    rs.getInt("NUM_CANCEL"),
                                    rs.getInt("MAX_ORDER"),
                                    rs.getDouble("WALLET"),
                                    rs.getDouble("RATING"),
                                    null, null, null,
                                    rs.getString("VIN"));
                        case CUSTOMER:
                            return new Customer(new Account(
                                    rs.getString("PHONE"),
                                    rs.getString("FULLNAME"),
                                    rs.getDate("DOB"),
                                    rs.getString("ROLE")),
                                    rs.getInt("NUM_SUCCESS"),
                                    rs.getInt("NUM_CANCEL"),
                                    rs.getDouble("WALLET"),
                                    null);
                        default:
                            return new Account(
                                    rs.getString("PHONE"),
                                    rs.getString("FULLNAME"),
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
                String sql = QueryStatement.selectAccount;
                if (role.toUpperCase().equals(SHIPPER)) {
                    sql = QueryStatement.selectShipperAccount;
                }
                sql = sql
                        + "WHERE ROLE = ? AND PHONE LIKE ?\n"
                        + "ORDER BY PHONE ASC\n"
                        + "OFFSET ? ROWS\n"
                        + "FETCH NEXT ? ROWS ONLY";
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
                            list.add(new Shipper(new Account(
                                    rs.getString("PHONE"),
                                    rs.getString("FULLNAME"),
                                    rs.getDate("DOB"),
                                    rs.getString("ROLE")),
                                    rs.getInt("NUM_SUCCESS"),
                                    rs.getInt("NUM_CANCEL"),
                                    rs.getInt("MAX_ORDER"),
                                    rs.getDouble("WALLET"),
                                    rs.getDouble("RATING"),
                                    null, null, null,
                                    rs.getString("VIN")));
                            break;
                        case CUSTOMER:
                            list.add(new Customer(new Account(
                                    rs.getString("PHONE"),
                                    rs.getString("FULLNAME"),
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
                String sql = QueryStatement.selectAccount;
                if (type.toUpperCase().equals(SHIPPER)) {
                    sql = QueryStatement.selectShipperAccount;
                }
                sql = sql
                        + "WHERE ROLE = ?\n"
                        + "ORDER BY PHONE ASC\n"
                        + "OFFSET ? ROWS\n"
                        + "FETCH NEXT ? ROWS ONLY";
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
                        list.add(new Shipper(new Account(
                                rs.getString("PHONE"),
                                rs.getString("FULLNAME"),
                                rs.getDate("DOB"),
                                rs.getString("ROLE")),
                                rs.getInt("NUM_SUCCESS"),
                                rs.getInt("NUM_CANCEL"),
                                rs.getInt("MAX_ORDER"),
                                rs.getDouble("WALLET"),
                                rs.getDouble("RATING"),
                                null, null, null,
                                rs.getString("VIN")));
                    } else if (type.toUpperCase().equals(CUSTOMER)) {
                        list.add(new Customer(new Account(
                                rs.getString("PHONE"),
                                rs.getString("FULLNAME"),
                                rs.getDate("DOB"),
                                rs.getString("ROLE")),
                                rs.getInt("NUM_SUCCESS"),
                                rs.getInt("NUM_CANCEL"),
                                rs.getDouble("WALLET"),
                                null));
                    } else {
                        list.add(new Account(
                                rs.getString("PHONE"),
                                rs.getString("FULLNAME"),
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
                String sql = QueryStatement.deleteAccount;
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
    public int deleteAddrCustomer(String username) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.deleteAddrAccount;
                stmt = con.prepareStatement(sql);
                stmt.setString(1, username);
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
                String sql = QueryStatement.updateWalletAccount;
                stmt = con.prepareStatement(sql);
                stmt.setString(1, accountId);
                stmt.setDouble(2, amount);
                stmt.setString(3, accountId);
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
    public int updateNumSuccess(String accountId, int num) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {

                String sql = QueryStatement.updateNumSuccess;
                stmt = con.prepareStatement(sql);
                stmt.setString(1, accountId);
                stmt.setInt(2, num);
                stmt.setString(3, accountId);
                return stmt.executeUpdate() > 0 ? 1 : 0;
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
    public int updateNumCancel(String accountId, int num) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {

                String sql = QueryStatement.updateNumCancel;
                stmt = con.prepareStatement(sql);
                stmt.setString(1, accountId);
                stmt.setInt(2, num);
                stmt.setString(3, accountId);
                return stmt.executeUpdate() > 0 ? 1 : 0;
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
    public int updateProfile(Account account) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.updateInfoAccount;
                stmt = con.prepareStatement(sql);
                stmt.setString(1, account.getFullname());
                stmt.setDate(2, account.getDob());
                stmt.setString(3, account.getUsername());
                return stmt.executeUpdate() > 0 ? 1 : 0;
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
    public int updateMaxNumOrder(String username, int maxNumOrder) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.updateMaxNumOrder;
                stmt = con.prepareStatement(sql);
                stmt.setInt(1, maxNumOrder);
                stmt.setString(2, username);
                return stmt.executeUpdate() > 0 ? 1 : 0;
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
    public int updatePassword(String username, String password) throws ClassNotFoundException, SQLException,
            NoSuchAlgorithmException, UnsupportedEncodingException {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.updatePassword;
                stmt = con.prepareStatement(sql);
                String salt = randomSalt();
                stmt.setString(1, encryptSHA(password + salt));
                stmt.setString(2, salt);
                stmt.setString(3, username);
                return stmt.executeUpdate() > 0 ? 1 : 0;
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
    public int insertAccount(AccountRegister account) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.insertAccount;
                stmt = con.prepareStatement(sql);
                String salt = randomSalt();
                stmt.setString(1, account.getUsername());
                stmt.setString(2, encryptSHA(account.getPassword() + salt));
                stmt.setString(3, salt);
                stmt.setString(4, account.getRole());
                stmt.setString(5, account.getFullname());
                stmt.setDate(6, account.getDob());
                stmt.setDate(7, new Date(new java.util.Date().getTime()));
                stmt.setDouble(8, 0);
                stmt.setInt(9, 0);
                stmt.setInt(10, 0);
                stmt.setBoolean(11, true);
                if (account.getRole().toUpperCase().equals(SHIPPER)) {
                    stmt.setString(12, account.getVin());
                    stmt.setInt(13, 1);
                } else {
                    stmt.setString(12, "");
                    stmt.setInt(13, -1);
                }
                return stmt.executeUpdate() > 0 ? 1 : 0;
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
    public int[] insertAddress(String username, List<Address> addresses) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        int[] result = null;

        try {
            con = DBUtils.getConnection();
            if (con != null) {
                String sql = QueryStatement.insertAddrAccount;
                stmt = con.prepareStatement(sql);
                for (Address address : addresses) {
                    stmt.setString(1, address.getAddr1().trim());
                    stmt.setString(2, address.getAddr2().trim());
                    stmt.setString(3, address.getAddr3().trim());
                    stmt.setString(4, address.getAddr4().trim());
                    stmt.setString(5, username);
                    stmt.addBatch();
                }
                result = stmt.executeBatch();
                con.commit();
            }
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }
}
