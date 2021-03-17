package com.bhanu.assignment.bank.model;

import java.util.List;

public class CustomerDto {
    private Customer customer;
    private List<Account> accounts;

    public CustomerDto() {
    }

    public CustomerDto(Customer customer, List<Account> accounts) {
        this.customer = customer;
        this.accounts = accounts;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
