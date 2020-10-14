package com.smhu.service.customer;

import com.smhu.enity.Customer;
import com.smhu.repository.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Customer findUser(String username, String password) {
        Customer result = customerRepository.findUserByUsernameAndPassword(username,password);
        return  result;
    }
}
