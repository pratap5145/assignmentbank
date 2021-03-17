package com.bhanu.assignment.bank.dao;

import com.bhanu.assignment.bank.model.AccountHistory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountHistoryDao extends CrudRepository<AccountHistory, Long> {
    List<AccountHistory> findAllByAcctId(Long acctId);
}
