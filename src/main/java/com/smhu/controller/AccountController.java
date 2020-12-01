package com.smhu.controller;

import com.smhu.account.Account;
import com.smhu.account.AccountLogin;
import com.smhu.account.AccountUpdateWallet;
import com.smhu.account.Customer;
import com.smhu.account.Shipper;
import com.smhu.dao.AccountDAO;
import com.smhu.dao.ShipperDAO;
import com.smhu.iface.IAccount;
import com.smhu.iface.IShipper;
import com.smhu.iface.ITransaction;
import com.smhu.response.ResponseMsg;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AccountController {

    final String STAFF = "STAFF";
    final String CUSTOMER = "CUSTOMER";
    final String SHIPPER = "SHIPPER";

    public static Map<String, String> mapRoles = new HashMap<>();

    private final IAccount service;
    private final IShipper shipperListener;

    public AccountController() {
        service = new AccountDAO();
        shipperListener = new ShipperDAO();
    }

    @CrossOrigin
    @GetMapping("/account/{type}/count")
    public ResponseEntity<?> getCountAccounts(@PathVariable("type") String type) {
        try {
            return new ResponseEntity<>(service.getCountAccounts(type), HttpStatus.OK);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, "Get All Account: {0}", e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/account/{type}/{search}/count")
    public ResponseEntity<?> getCountAccounts(@PathVariable("type") String type, @PathVariable("search") String search) {
        try {
            return new ResponseEntity<>(service.getCountAccounts(type, search), HttpStatus.OK);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, "Get All Account: {0}", e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/accounts/{type}/{page}")
    public ResponseEntity<?> getAllAccounts(@PathVariable("type") String type, @PathVariable("page") String page) {
        try {
            List<?> result = service.getAccounts(type, page);
            if (result != null) {
                for (Object account : result) {
                    if (account instanceof Customer) {
                        ((Customer) account).setAddresses(service.getAddressOfAccount(((Customer) account).getId()));
                    }
                }
            }

            return new ResponseEntity<>(service.getAccounts(type, page), HttpStatus.OK);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, "Get All Account: {0}", e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/account/{type}/{search}/{page}")
    public ResponseEntity<?> getAccountsBySearch(@PathVariable("type") String type, @PathVariable("search") String search,
            @PathVariable("page") String page) {
        try {
            return new ResponseEntity<>(service.getAccountBySearch(type, search, page), HttpStatus.OK);
        } catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, "Get Account By Id: {0}", e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/account/{accountId}/{type}")
    public ResponseEntity<?> getAccountById(@PathVariable("accountId") String id, @PathVariable("type") String type) {
        try {
            Object account = service.getAccountById(id, type);
            if (account == null) {
                return new ResponseEntity<>(new ResponseMsg("Account ID is not correct"), HttpStatus.NOT_FOUND);
            }

            if (account instanceof Shipper) {
                return new ResponseEntity<>((Shipper) account, HttpStatus.OK);
            } else if (account instanceof Customer) {
                Customer customer = (Customer) account;
                customer.setAddresses(service.getAddressOfAccount(customer.getId()));
                return new ResponseEntity<>(customer, HttpStatus.OK);
            } else if (account instanceof Account) {
                return new ResponseEntity<>((Account) account, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }
        } catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, "Get Account By Id: {0}", e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @PostMapping("/account/username")
    public ResponseEntity<?> getAccountByUsername(@RequestBody AccountLogin accountObj) {
        Object obj;
        try {
            if (!mapRoles.containsKey(accountObj.getRole().toLowerCase())) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }

            String encrypt = service.getEncryptionPassword(accountObj.getUsername(), accountObj.getRole().toLowerCase());
            if (encrypt == null) {
                return new ResponseEntity<>(new ResponseMsg("Username or Password is not correct"), HttpStatus.NOT_FOUND);
            }

            String encodePassword = service.encryptSHA(accountObj.getPassword() + encrypt);
            obj = service.getAccountByUsername(accountObj.getUsername(), encodePassword, accountObj.getRole());
            if (obj == null) {
                return new ResponseEntity<>(new ResponseMsg("Username or Password is not correct"), HttpStatus.NOT_FOUND);
            }

            if (obj instanceof Shipper) {
                Shipper shipper = (Shipper) obj;
                shipperListener.addShipper(shipper);

                System.out.println("Login: " + shipper);
                System.out.println("");

                return new ResponseEntity<>(shipper, HttpStatus.OK);
            } else if (obj instanceof Customer) {
                Customer account = (Customer) obj;
                account.setAddresses(service.getAddressOfAccount(account.getId()));
                System.out.println("Login: " + account);
                System.out.println("");

                return new ResponseEntity<>(account, HttpStatus.OK);
            } else if (obj instanceof Account) {
                Account account = (Account) obj;
                return new ResponseEntity<>(account, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }
        } catch (SQLException | ClassNotFoundException
                | NoSuchAlgorithmException | UnsupportedEncodingException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @GetMapping("/account/phone/{phone}/role/{role}")
//    public ResponseEntity<?> getAccountByPhone(@PathVariable("phone") String phone, @PathVariable("role") String role) {
//        Account account = null;
//
//        try {
//            if (mapRoles.containsKey(role)) {
//                account = service.getAccountByEmailOrPhone(phone.toLowerCase(), role, service.PHONE);
//            } else {
//                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
//            }
//
//            if (account == null) {
//                return new ResponseEntity<>(new ResponseMsg("Phone number is not correct"), HttpStatus.NOT_FOUND);
//            }
//            account.setAddresses(service.getAddressOfAccount(account.getId()));
//        } catch (SQLException | ClassNotFoundException e) {
//            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, e.getMessage());
//            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity<>(account, HttpStatus.OK);
//    }
//
//    @GetMapping("/account/email/{email}/role/{role}")
//    public ResponseEntity<?> getAccountByEmail(@PathVariable("email") String email, @PathVariable("role") String role) {
//        Account account = null;
//
//        try {
//            if (mapRoles.containsKey(role)) {
//                account = service.getAccountByEmailOrPhone(email.toLowerCase(), role, service.EMAIL);
//            } else {
//                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
//            }
//
//            if (account == null) {
//                return new ResponseEntity<>(new ResponseMsg("Email is not correct"), HttpStatus.NOT_FOUND);
//            }
//            account.setAddresses(service.getAddressOfAccount(account.getId()));
//        } catch (SQLException | ClassNotFoundException e) {
//            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, e.getMessage());
//            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity<>(account, HttpStatus.OK);
//    }
    @CrossOrigin
    @DeleteMapping("/delete/{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable("accountId") String id) {
        try {
            int result = service.deleteAccount(id, false);
            if (result <= 0) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }
        } catch (ClassNotFoundException | SQLException | NumberFormatException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, "Delete Account: {0}", e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin
    @PutMapping("/account")
    public ResponseEntity<?> updateWalletAccount(@RequestBody AccountUpdateWallet account) {
        try {
            int result = service.updateWalletAccount(account.getId(), account.getAmount());
            if (result <= 0) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }
            ITransaction transactionListener = new TransactionController().getTransactionListener();
            int checkTransaction = transactionListener.updateRechargeTransaction(account.getId(), account.getAmount());
            if (checkTransaction <= 0) {
                return new ResponseEntity<>(new ResponseMsg("Update Traction Error. Please try again!"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (ClassNotFoundException | SQLException | NumberFormatException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, "Update Wallet: {0}", e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    class AccountService {

        

//        Account getAccountByEmailOrPhone(String param, String role, String type) throws SQLException, ClassNotFoundException {
//            if (!AccountController.mapRoles.containsKey(role)) {
//                return null;
//            }
//
//            Connection con = null;
//            PreparedStatement stmt = null;
//            ResultSet rs = null;
//            StringBuilder sql = new StringBuilder();
//
//            try {
//                con = DBUtils.getConnection();
//                if (con != null) {
//                    sql.append("SELECT *")
//                            .append("\n");
//                    sql.append("FROM GET_ACCOUNT")
//                            .append("\n");
//                    sql.append("WHERE ROLE = ?")
//                            .append("\n");
//                    switch (type) {
//                        case EMAIL:
//                            sql.append("AND EMAIL = ?");
//                            break;
//                        case PHONE:
//                            sql.append("AND PHONE = ?");
//                            break;
//                        default:
//                            return null;
//                    }
//
//                    stmt = con.prepareStatement(sql.toString());
//                    stmt.setString(1, role);
//                    stmt.setString(2, param);
//                    rs = stmt.executeQuery();
//                    if (rs.next()) {
//                        return new Account(rs.getString("ID"),
//                                rs.getString("USERNAME"),
//                                rs.getString("FIRST_NAME"),
//                                rs.getString("MID_NAME"),
//                                rs.getString("LAST_NAME"),
//                                rs.getString("EMAIL"),
//                                rs.getString("PHONE"),
//                                rs.getDate("DOB"),
//                                rs.getString("ROLE"),
//                                rs.getInt("NUM_SUCCESS"),
//                                rs.getInt("NUM_CANCEL"),
//                                rs.getDouble("WALLET"),
//                                null);
//                    }
//                }
//            } finally {
//                if (rs != null) {
//                    rs.close();
//                }
//                if (stmt != null) {
//                    stmt.close();
//                }
//                if (con != null) {
//                    con.close();
//                }
//            }
//            return null;
//        }
        
    }
}
