package com.bhanu.assignment.bank.service;

import com.bhanu.assignment.bank.model.AccountHistory;

import java.util.List;

public interface AccountHistoryService {
    public AccountHistory save(AccountHistory accountHistory);
    public List<AccountHistory> findAllByAcctId(long id);
}
