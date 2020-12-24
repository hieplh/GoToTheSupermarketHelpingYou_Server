package com.smhu.controller;

import com.smhu.account.Account;
import com.smhu.account.AccountLogin;
import com.smhu.account.AccountRegister;
import com.smhu.account.AccountUpdate;
import com.smhu.account.Customer;
import com.smhu.account.CustomerUpdateAddress;
import com.smhu.account.Shipper;
import com.smhu.account.ShipperUpdateMaxNumOrder;
import com.smhu.core.CoreFunctions;
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
@CrossOrigin
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

    @GetMapping("/account/{type}/count")
    public ResponseEntity<?> getCountAccounts(@PathVariable("type") String type) {
        try {
            return new ResponseEntity<>(service.getCountAccounts(type), HttpStatus.OK);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, "Get All Account: {0}", e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/account/{type}/{search}/count")
    public ResponseEntity<?> getCountAccounts(@PathVariable("type") String type, @PathVariable("search") String search) {
        try {
            return new ResponseEntity<>(service.getCountAccounts(type, search), HttpStatus.OK);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, "Get All Account: {0}", e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/accounts/{type}/{page}")
    public ResponseEntity<?> getAllAccounts(@PathVariable("type") String type, @PathVariable("page") String page) {
        try {
            List<?> result = service.getAccounts(type, page);
            if (result != null) {
                for (Object account : result) {
                    if (account instanceof Customer) {
                        ((Customer) account).setAddresses(service.getAddressOfAccount(((Customer) account).getUsername()));
                    }
                }
            }

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, "Get All Account: {0}", e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
                customer.setAddresses(service.getAddressOfAccount(customer.getUsername()));
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

    @GetMapping("/account/{username}/logout")
    public ResponseEntity<?> logoutAccount(@PathVariable("username") String username) {
        Shipper shipper = shipperListener.getShipper(username);
        if (shipper == null) {
            return new ResponseEntity<>("Account is not existed.", HttpStatus.METHOD_NOT_ALLOWED);
        }
        try {
            if (ShipperController.mapShipperOrdersInProgress.containsKey(shipper.getUsername())) {
                return new ResponseEntity<>("You must be done all orders to logout.", HttpStatus.METHOD_NOT_ALLOWED);
            }

            synchronized (CoreFunctions.mapFilterShippers) {
                for (Map.Entry<String, List<String>> entry : CoreFunctions.mapFilterShippers.entrySet()) {
                    List<String> shippers = entry.getValue();
                    if (shippers != null) {
                        for (String object : shippers) {
                            if (shipper.getUsername().equals(object)) {
                                entry.getValue().remove(shipper.getUsername());
                            }
                        }
                    }
                }
            }
            shipperListener.removeShipper(shipper.getUsername());
        } catch (Exception e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Logout success", HttpStatus.OK);
    }

    @PostMapping("/account/username")
    public ResponseEntity<?> getAccountByUsername(@RequestBody AccountLogin accountObj) {
        Object obj;
        try {
            if (!mapRoles.containsKey(accountObj.getRole().toLowerCase())) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }

            String encrypt = service.getEncryptionPassword(accountObj.getUsername());
            if (encrypt == null) {
                return new ResponseEntity<>(new ResponseMsg("Username or Password is not correct"), HttpStatus.CONFLICT);
            }

            String encodePassword = service.encryptSHA(accountObj.getPassword() + encrypt);
            obj = service.getAccountByUsername(accountObj.getUsername(), encodePassword, accountObj.getRole());
            if (obj == null) {
                return new ResponseEntity<>(new ResponseMsg("Username or Password is not correct"), HttpStatus.CONFLICT);
            }

            if (obj instanceof Shipper) {
                Shipper shipper = (Shipper) obj;
                shipperListener.addShipper(shipper);

                System.out.println("Login: " + shipper);
                System.out.println("");

                return new ResponseEntity<>(shipper, HttpStatus.OK);
            } else if (obj instanceof Customer) {
                Customer account = (Customer) obj;
                account.setAddresses(service.getAddressOfAccount(account.getUsername()));
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

    @PostMapping("/account/register")
    public ResponseEntity<?> registerAccount(@RequestBody AccountRegister account) {
        try {
            System.out.println("Account: " + account);
            if (account == null) {
                return new ResponseEntity<>("Account is null", HttpStatus.METHOD_NOT_ALLOWED);
            }
            if (!mapRoles.containsKey(account.getRole().toLowerCase())) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }

            String encrypted = service.getEncryptionPassword(account.getUsername());
            if (encrypted != null) {
                return new ResponseEntity<>("Username is existed. Please try again!", HttpStatus.CONFLICT);
            }
            boolean isDuplicatePhoneOrVin = service.checkExistedPhoneOrVin(account.getPhone(), account.getVin());
            if (isDuplicatePhoneOrVin) {
                return new ResponseEntity<>("Phone or Vin is existed. Please try again!", HttpStatus.CONFLICT);
            }

            int result = service.insertAccount(account);
            if (result > 0) {
                if (account.getRole().toUpperCase().equals(SHIPPER)) {
                    service.insertMaxNumOrder(account.getUsername(), 1);
                }
            }
        } catch (ClassNotFoundException | SQLException
                | NoSuchAlgorithmException | UnsupportedEncodingException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Registered Success", HttpStatus.OK);
    }

    @PutMapping("/account/wallet")
    public ResponseEntity<?> updateWalletAccount(@RequestBody AccountUpdate account) {
        try {
            int result = service.updateWalletAccount(account.getUsername(), account.getAmount());
            if (result < 0) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }
            ITransaction transactionListener = new TransactionController().getTransactionListener();
            int checkTransaction = transactionListener.updateRechargeTransaction(account.getUsername(), account.getAmount());
            if (checkTransaction <= 0) {
                return new ResponseEntity<>(new ResponseMsg("Update Traction Error. Please try again!"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (ClassNotFoundException | SQLException | NumberFormatException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, "Update Wallet: {0}", e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/account/info")
    public ResponseEntity<?> updateInfo(@RequestBody Account account) {
        try {
            if (!mapRoles.containsKey(account.getRole().toLowerCase())) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }
            service.updateProfile(account);
        } catch (ClassNotFoundException | SQLException | NumberFormatException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, "Update Info: {0}", e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Update Success", HttpStatus.OK);
    }

    @PutMapping("/account/address")
    public ResponseEntity<?> updateAddress(@RequestBody CustomerUpdateAddress account) {
        try {
            String role = account.getRole().toUpperCase();
            switch (role) {
                case CUSTOMER:
                    service.deleteAddrCustomer(account.getUsername());
                    int[] arrResult = service.insertAddress(account.getUsername(), account.getAddresses());
                    if (arrResult == null) {
                        return new ResponseEntity<>("Update Address failed", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    break;
                default:
                    return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }
        } catch (ClassNotFoundException | SQLException | NumberFormatException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, "Update Wallet: {0}", e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Update Success", HttpStatus.OK);
    }

    @PutMapping("/account/maxnumorder")
    public ResponseEntity<?> updateMaxNumOrder(@RequestBody ShipperUpdateMaxNumOrder account) {
        try {
            if (!mapRoles.containsKey(account.getRole().toLowerCase())) {
                return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }

            String role = account.getRole().toUpperCase();
            switch (role) {
                case SHIPPER:
                    int result = service.updateMaxNumOrder(account.getUsername(), account.getMaxNumOrder());
                    if (result <= 0) {
                        return new ResponseEntity<>("Update max num Order failed", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    break;
                default:
                    return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.toString(), HttpStatus.METHOD_NOT_ALLOWED);
            }
        } catch (ClassNotFoundException | SQLException | NumberFormatException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, "Update Wallet: {0}", e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Update Success", HttpStatus.OK);
    }

    @PutMapping("/account/password")
    public ResponseEntity<?> updatePassword(@RequestBody AccountUpdate account) {
        try {
            String salt = service.getEncryptionPassword(account.getUsername());
            if (salt == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED.toString(), HttpStatus.UNAUTHORIZED);
            }

            String encryptedPwd = service.encryptSHA(account.getOldPwd() + salt);
            Account obj = (Account) service.getAccountByUsername(account.getUsername(), encryptedPwd, account.getRole());
            if (obj == null) {
                return new ResponseEntity<>("Password is not correct", HttpStatus.CONFLICT);
            }

            int result = service.updatePassword(account.getUsername(), account.getNewPwd());
            if (result <= 0) {
                return new ResponseEntity<>("Update password failed. Please try again", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (ClassNotFoundException | SQLException
                | NumberFormatException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, "Update Password: {0}", e.getMessage());
            return new ResponseEntity<>(new ResponseMsg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Update pwd Success", HttpStatus.OK);
    }

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
}
