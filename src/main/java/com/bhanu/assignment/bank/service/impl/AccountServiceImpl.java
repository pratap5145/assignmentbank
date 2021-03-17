package com.bhanu.assignment.bank.service.impl;

import com.bhanu.assignment.bank.dao.AccountDao;
import com.bhanu.assignment.bank.model.Account;
import com.bhanu.assignment.bank.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountDao accountDao;

    @Override
    public Account findAccountById(long id) throws Exception {
        Optional<Account> account = accountDao.findById(id);
        if(!account.isPresent())
            throw new Exception("Customer doesn't exists");
        return account.get();
    }

    @Override
    public Account save(Account account) {
        return accountDao.save(account);
    }

    @Override
    public void delete(Account account) {
        accountDao.delete(account);
    }

    @Override
    public List<Account> findAllAccountForCustomer(long customerId) {
        return accountDao.findByCustomerId(customerId);
    }

    @Override
    public Iterable<Account> findAllAccounts() {
        return accountDao.findAll();
    }
}
