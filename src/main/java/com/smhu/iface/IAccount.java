package com.smhu.iface;

import com.smhu.account.Account;
import com.smhu.account.AccountRegister;
import com.smhu.account.Address;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IAccount {

    public String getEncryptionPassword(String username, String type) throws SQLException, ClassNotFoundException;

    public String encryptSHA(String source) throws NoSuchAlgorithmException, UnsupportedEncodingException;

    public int getCountAccounts(String role) throws SQLException, ClassNotFoundException;

    public int getCountAccounts(String role, String search) throws SQLException, ClassNotFoundException;

    public Object getAccountByUsername(String username, String password, String type) throws SQLException, ClassNotFoundException;

    public List<Address> getAddressOfAccount(String accountId) throws ClassNotFoundException, SQLException;

    public List<? super Account> getAccountBySearch(String role, String search, String page) throws SQLException, ClassNotFoundException;

    public List<? super Account> getAccounts(String type, String page) throws SQLException, ClassNotFoundException;

    public int deleteAccount(String accountId, boolean flag) throws ClassNotFoundException, SQLException;

    public Map<String, String> getRoles() throws SQLException, ClassNotFoundException;

    public Object getAccountById(String id, String type) throws SQLException, ClassNotFoundException;

    public int updateWalletAccount(String accountId, double amount) throws ClassNotFoundException, SQLException;

    public int updateNumSuccess(String accountId, int num) throws ClassNotFoundException, SQLException;

    public int updateNumCancel(String accountId, int num) throws ClassNotFoundException, SQLException;

    public int insertAccount(AccountRegister account, String vin) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, UnsupportedEncodingException;
    
    public int insertMaxNumOrder(String username, int num) throws ClassNotFoundException, SQLException;
    
    public int updateProfile(Account account) throws ClassNotFoundException, SQLException;
    
    public int updateMaxNumOrder(String username, int maxNumOrder) throws ClassNotFoundException, SQLException;
    
    public int[] insertAddress(String username, List<Address> addresses) throws ClassNotFoundException, SQLException;
    
    public int deleteAddrCustomer(String username) throws ClassNotFoundException, SQLException;
}
