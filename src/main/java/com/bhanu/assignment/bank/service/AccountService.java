package com.bhanu.assignment.bank.service;

import com.bhanu.assignment.bank.model.Account;
import com.bhanu.assignment.bank.model.Customer;

import java.util.List;

public interface AccountService {
    public Account findAccountById(long id) throws Exception;
    public Account save(Account account);
    public void delete(Account account);
    public List<Account> findAllAccountForCustomer(long customerId);
    Iterable<Account> findAllAccounts();
}
