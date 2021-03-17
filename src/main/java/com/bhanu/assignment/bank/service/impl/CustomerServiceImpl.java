package com.bhanu.assignment.bank.service.impl;

import com.bhanu.assignment.bank.dao.CustomerDao;
import com.bhanu.assignment.bank.model.Customer;
import com.bhanu.assignment.bank.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerDao customerDao;
    @Override
    public Customer findCustomerById(long id) throws Exception {
        Optional<Customer> customer = customerDao.findById(id);
        if(!customer.isPresent())
            throw new Exception("Customer doesn't exists");
        return customer.get();
    }

    @Override
    public Customer save(Customer customer) {
        return customerDao.save(customer);
    }

    @Override
    public void delete(Customer customer) {
        customerDao.delete(customer);
    }
}
