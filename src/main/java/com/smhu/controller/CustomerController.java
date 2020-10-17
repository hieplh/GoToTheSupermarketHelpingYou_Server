//package com.smhu.controller;
//
//import com.smhu.enity.Customer;
//import com.smhu.enity.dto.LoginDTO;
//import com.smhu.service.customer.CustomerService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api")
//public class CustomerController {
//
//    @Autowired(required = true)
//    public CustomerService customerService;
//
//    @PostMapping("/customer/login")
//    public ResponseEntity<?> findCustomer(@RequestBody LoginDTO requestBody) {
//        String username = requestBody.getUsername();
//        String password = requestBody.getPassword();
//        Customer result = customerService.findUser(username, password);
//        if (result != null) {
//            return ResponseEntity.ok(result);
//        } else {
//            return new ResponseEntity(HttpStatus.NOT_FOUND);
//        }
//    }
//}
