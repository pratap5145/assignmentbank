package com.bhanu.assignment.bank.service;

import com.bhanu.assignment.bank.model.Customer;

public interface CustomerService {
    public Customer findCustomerById(long id) throws Exception;
    public Customer save(Customer customer);
    public void delete(Customer customer);
    Customer findCustomerIfExists(long id);
}
