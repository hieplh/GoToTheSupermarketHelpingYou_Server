package com.smhu.service.customer;


import com.smhu.enity.Customer;
import org.springframework.stereotype.Service;

@Service
public interface CustomerService {

    public Customer findUser(String username, String password);
}
