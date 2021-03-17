package com.bhanu.assignment.bank.service.impl;

import com.bhanu.assignment.bank.dao.AccountHistoryDao;
import com.bhanu.assignment.bank.model.AccountHistory;
import com.bhanu.assignment.bank.service.AccountHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AccountHistoryServiceImpl implements AccountHistoryService {

    @Autowired
    private AccountHistoryDao accountHistoryDao;

    @Override
    public AccountHistory save(AccountHistory accountHistory) {
        return accountHistoryDao.save(accountHistory);
    }

    public List<AccountHistory> findAllByAcctId(long id) {
        return accountHistoryDao.findAllByAcctId(id);
    }
}
