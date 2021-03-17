package com.bhanu.assignment.bank.model;

public class TransferMoney {
    private long customer1;
    private long account1;
    private long customer2;
    private long account2;
    private long amount;

    public TransferMoney() {
    }

    public TransferMoney(long customer1, long account1, long customer2, long account2, long amount) {
        this.customer1 = customer1;
        this.account1 = account1;
        this.customer2 = customer2;
        this.account2 = account2;
        this.amount = amount;
    }

    public long getCustomer1() {
        return customer1;
    }

    public long getAccount1() {
        return account1;
    }

    public long getCustomer2() {
        return customer2;
    }

    public long getAccount2() {
        return account2;
    }

    public long getAmount() {
        return amount;
    }

    public void setCustomer1(long customer1) {
        this.customer1 = customer1;
    }

    public void setAccount1(long account1) {
        this.account1 = account1;
    }

    public void setCustomer2(long customer2) {
        this.customer2 = customer2;
    }

    public void setAccount2(long account2) {
        this.account2 = account2;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
