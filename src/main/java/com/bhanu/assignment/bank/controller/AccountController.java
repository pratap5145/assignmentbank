package com.bhanu.assignment.bank.controller;

import com.bhanu.assignment.bank.dao.AccountDao;
import com.bhanu.assignment.bank.dao.AccountHistoryDao;
import com.bhanu.assignment.bank.dao.CustomerDao;
import com.bhanu.assignment.bank.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private AccountHistoryDao accountHistoryDao;

    @GetMapping("/testUser")
    public ResponseEntity<Customer> getUser(@RequestParam("id") Long id){
        Optional<Customer> customer = customerDao.findById(id);
        return null;
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/addCustomer")
    public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer) throws Exception {
        Optional<Customer> customerFromDB = customerDao.findById(customer.getId());
        if(customerFromDB.isPresent()){
            throw new Exception("User Already Exists.");
        }
        customerDao.save(customer);
        return new ResponseEntity<Customer>(customer, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/deleteCustomer")
    public ResponseEntity<Customer> deleteCustomer(@RequestParam("id") long id) throws Exception {
        Optional<Customer> customerFromDB = customerDao.findById(id);
        if(customerFromDB.isPresent()){
            if(accountDao.findByCustomerId(customerFromDB.get().getId()).size()>0){
                throw new Exception("Delete all accounts under the customer : "+customerFromDB.toString());
            }
            customerDao.delete(customerFromDB.get());
            return new ResponseEntity<Customer>(customerFromDB.get(), HttpStatus.OK);
        }
        throw new Exception("User not found for id : "+id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addAccount")
    public ResponseEntity<Account> addCustomer(@RequestBody Account account) throws Exception {
        if(!customerDao.findById(account.getCustomer().getId()).isPresent()){
            throw new Exception("User doesn't exist");
        }
        Timestamp current = new Timestamp(System.currentTimeMillis());
        List<Account> accountDaoByCustomerId = accountDao.findByCustomerId(account.getCustomer().getId());
        if(accountDaoByCustomerId.size()>0){
            List<Account> accounts = accountDaoByCustomerId.stream()
                    .filter(account1 -> account1.getAcct_type().equals(account.getAcct_type()))
                    .collect(Collectors.toList());
            if(!accounts.isEmpty())
                throw new Exception("Account Already Exists.");
        }
        //set time
        account.setExecutionTime(current);
        accountDao.save(account);
        List<Account> account1 = accountDao.findByCustomerId(account.getCustomer().getId());
        Optional<Account> tempAccount = account1.stream().filter(account2 -> account2.getAcct_type()==account.getAcct_type()).findFirst();
        if(tempAccount.isPresent()) {
            Account temp = tempAccount.get();
            temp.setExecutionTime(current);
            accountHistoryDao.save(new AccountHistory(-1, temp.getId(), temp.getAcct_type(), temp.getBalance(), temp.getExecutionTime(), temp.getCustomer()));
        }
        return new ResponseEntity<Account>(account, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteAccount")
    public ResponseEntity<Account> deleteAccount(@RequestParam("id") long id) throws Exception {
        Optional<Account> account = accountDao.findById(id);
        if(account.isPresent()){
            accountDao.delete(account.get());
            return new ResponseEntity<Account>(account.get(), HttpStatus.OK);
        }
        throw new Exception("Account doesn't exists.");
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/getUserDetails")
    public ResponseEntity<CustomerDto> getUserDetails(@RequestParam("id") long id) throws Exception {
        Optional<Customer> customerFromDB = customerDao.findById(id);
        if(customerFromDB.isPresent()){
            long customerId = customerFromDB.get().getId();
            List<Account> accounts = accountDao.findByCustomerId(customerId);
            return new ResponseEntity<CustomerDto>(new CustomerDto(customerFromDB.get(),accounts),HttpStatus.OK);
        }
        throw new Exception("User not found for id : "+id);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/getBalance")
    public ResponseEntity<Account> getBalance(@RequestParam("customerId") long customerId, @RequestParam("acct_id") long accountId) throws Exception {
        Optional<Customer> customer = customerDao.findById(customerId);
        if(!customer.isPresent())
            throw new Exception("Customer doesn't exists");
        Optional<Account> account = accountDao.findById(accountId);
        if(!account.isPresent())
            throw new Exception("Account doesn't exists");
        return new ResponseEntity<>(account.get(),HttpStatus.OK);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/transfer")
    public ResponseEntity<Account> transfer(@RequestBody TransferMoney transferMoney) throws Exception {
        Optional<Customer> customer1 = customerDao.findById(transferMoney.getCustomer1());
        if(!customer1.isPresent())
            throw new Exception("Customer doesn't exists");
        Optional<Customer> customer2 = customerDao.findById(transferMoney.getCustomer2());
        if(!customer2.isPresent())
            throw new Exception("Customer doesn't exists");
        Optional<Account> account1 = accountDao.findById(transferMoney.getAccount1());
        if(!account1.isPresent())
            throw new Exception("Account doesn't exists");
        Optional<Account> account2 = accountDao.findById(transferMoney.getAccount2());
        if(!account2.isPresent())
            throw new Exception("Account doesn't exists");
        double senderBalance = account1.get().getBalance();
        if(senderBalance<transferMoney.getAmount())
            throw new Exception("Insufficient Balance");
        account1.get().setBalance(account1.get().getBalance()-transferMoney.getAmount());
        account2.get().setBalance(account2.get().getBalance()+transferMoney.getAmount());
        Timestamp current = new Timestamp(System.currentTimeMillis());
        account1.get().setExecutionTime(current);
        account2.get().setExecutionTime(current);
        accountDao.save(account1.get());
        accountDao.save(account2.get());
        accountHistoryDao.save(new AccountHistory(-1,account1.get().getId(),account1.get().getAcct_type(),account1.get().getBalance(),current,account1.get().getCustomer()));
        accountHistoryDao.save(new AccountHistory(-1,account2.get().getId(),account2.get().getAcct_type(),account2.get().getBalance(),current,account2.get().getCustomer()));
        return new ResponseEntity<>(account2.get(),HttpStatus.OK);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/getAccountStatement")
    public ResponseEntity<List<AccountHistory>> getAccountStatement(@RequestParam("id") long id, @RequestParam("acct_type") String acct_type) throws Exception {
        Optional<Account> account = accountDao.findById(id);
        if(!account.isPresent())
            throw new Exception("Account doesn't exists");
        List<AccountHistory> allByAcctId = accountHistoryDao.findAllByAcctId(id);
        allByAcctId = allByAcctId.stream().filter(accountHistory -> accountHistory.getAcct_type().equals(acct_type)).collect(Collectors.toList());
        Collections.sort(allByAcctId, new Comparator<AccountHistory>() {
            @Override
            public int compare(AccountHistory o1, AccountHistory o2) {
                return o1.getExecutionTime().compareTo(o2.getExecutionTime());
            }
        });
        return new ResponseEntity<>(allByAcctId,HttpStatus.OK);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/calculateInterest")
    public ResponseEntity<?> calculateInterest(){
        Iterable<Account> accounts = accountDao.findAll();
        for (Account account: accounts) {
            double amount = 1.035*account.getBalance();
            account.setBalance(amount);
            Timestamp current = new Timestamp(System.currentTimeMillis());
            account.setExecutionTime(current);
            accountDao.save(account);
            accountHistoryDao.save(new AccountHistory(-1,account.getId(),account.getAcct_type(),amount,account.getExecutionTime(),account.getCustomer()));
        }
        return ResponseEntity.ok("Calculated Interest");
    }

}
