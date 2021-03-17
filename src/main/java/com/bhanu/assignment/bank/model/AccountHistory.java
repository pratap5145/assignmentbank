package com.bhanu.assignment.bank.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class AccountHistory {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    public AccountHistory(long id, long acctId, String acct_type, double balance, Timestamp executionTime, Customer customer) {
        this.id = id;
        this.acctId = acctId;
        this.acct_type = acct_type;
        this.balance = balance;
        this.executionTime = executionTime;
        this.customer = customer;
    }

    public AccountHistory() {
    }

    public long getAcctId() {
        return acctId;
    }

    public void setAcctId(int acctId) {
        this.acctId = acctId;
    }

    @Column(name="acctid")
    private long acctId;

    @Column(name="acct_type")
    private String acct_type;

    @Column(name = "balance")
    private double balance;

    @Column(name="execution_time")
    private Timestamp executionTime;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    private Customer customer;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAcct_type() {
        return acct_type;
    }

    public void setAcct_type(String acct_type) {
        this.acct_type = acct_type;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Timestamp getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Timestamp executionTime) {
        this.executionTime = executionTime;
    }
}
