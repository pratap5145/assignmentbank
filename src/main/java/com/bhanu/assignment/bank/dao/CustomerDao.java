package com.bhanu.assignment.bank.dao;

import com.bhanu.assignment.bank.model.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerDao extends CrudRepository<Customer,Long> {
    List<Customer> findAllById(Long id);
}
