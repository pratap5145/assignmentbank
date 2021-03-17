package com.bhanu.assignment.bank.dao;

import com.bhanu.assignment.bank.model.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountDao extends CrudRepository<Account, Long> {
    List<Account> findByCustomerId(Long customerId);
}
