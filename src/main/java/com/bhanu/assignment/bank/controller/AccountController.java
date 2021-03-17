package com.bhanu.assignment.bank.controller;

import com.bhanu.assignment.bank.model.*;
import com.bhanu.assignment.bank.service.impl.AccountHistoryServiceImpl;
import com.bhanu.assignment.bank.service.impl.AccountServiceImpl;
import com.bhanu.assignment.bank.service.impl.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

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
    private CustomerServiceImpl customerService;

    @Autowired
    private AccountServiceImpl accountService;

    @Autowired
    private AccountHistoryServiceImpl accountHistoryService;

    @GetMapping("/testUser")
    public ResponseEntity<Customer> getUser(@RequestParam("id") Long id) throws Exception {
        Customer customer = customerService.findCustomerById(id);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/addCustomer")
    public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer) throws Exception {
        Customer customer1 = customerService.save(customer);
        return new ResponseEntity<Customer>(customer1, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/deleteCustomer")
    public ResponseEntity<Customer> deleteCustomer(@RequestParam("id") long id) throws Exception {
        Customer customerFromDB = customerService.findCustomerById(id);
        if(customerFromDB!=null){
            if(accountService.findAllAccountForCustomer(customerFromDB.getId()).size()>0){
                throw new Exception("Delete all accounts under the customer : "+customerFromDB.toString());
            }
            customerService.delete(customerFromDB);
            return new ResponseEntity<Customer>(customerFromDB, HttpStatus.OK);
        }
        throw new Exception("User not found for id : "+id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addAccount")
    public ResponseEntity<Account> addCustomer(@RequestBody Account account) throws Exception {
        if(customerService.findCustomerById(account.getCustomer().getId())==null){
            throw new Exception("Customer doesn't exist");
        }
        Timestamp current = new Timestamp(System.currentTimeMillis());
        List<Account> accountDaoByCustomerId = accountService.findAllAccountForCustomer(account.getCustomer().getId());
        if(accountDaoByCustomerId.size()>0){
            List<Account> accounts = accountDaoByCustomerId.stream()
                    .filter(account1 -> account1.getAcct_type().equals(account.getAcct_type()))
                    .collect(Collectors.toList());
            if(!accounts.isEmpty())
                throw new Exception("Account Already Exists.");
        }
        //set time
        account.setExecutionTime(current);
        Account accountRet = accountService.save(account);
        List<Account> account1 = accountService.findAllAccountForCustomer(account.getCustomer().getId());
        Optional<Account> tempAccount = account1.stream().filter(account2 -> account2.getAcct_type()==account.getAcct_type()).findFirst();
        if(tempAccount.isPresent()) {
            Account temp = tempAccount.get();
            temp.setExecutionTime(current);
            accountHistoryService.save(new AccountHistory(-1, temp.getId(), temp.getAcct_type(), temp.getBalance(), temp.getExecutionTime(), temp.getCustomer()));
        }
        return new ResponseEntity<Account>(accountRet, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteAccount")
    public ResponseEntity<Account> deleteAccount(@RequestParam("id") long id) throws Exception {
        Account account = accountService.findAccountById(id);
        if(account!=null){
            accountService.delete(account);
            return new ResponseEntity<Account>(account, HttpStatus.OK);
        }
        throw new Exception("Account doesn't exists.");
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/getUserDetails")
    public ResponseEntity<CustomerDto> getUserDetails(@RequestParam("id") long id) throws Exception {
        Customer customerFromDB = customerService.findCustomerById(id);
        if(customerFromDB!=null){
            long customerId = customerFromDB.getId();
            List<Account> accounts = accountService.findAllAccountForCustomer(customerId);
            return new ResponseEntity<CustomerDto>(new CustomerDto(customerFromDB,accounts),HttpStatus.OK);
        }
        throw new Exception("User not found for id : "+id);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/getBalance")
    public ResponseEntity<Account> getBalance(@RequestParam("customerId") long customerId, @RequestParam("acct_id") long accountId) throws Exception {
        Customer customer = customerService.findCustomerById(customerId);
        Account account = accountService.findAccountById(accountId);
        return new ResponseEntity<>(account,HttpStatus.OK);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/transfer")
    public ResponseEntity<Account> transfer(@RequestBody TransferMoney transferMoney) throws Exception {
        Customer customer1 = customerService.findCustomerById(transferMoney.getCustomer1());
        Customer customer2 = customerService.findCustomerById(transferMoney.getCustomer1());
        Account account1 = accountService.findAccountById(transferMoney.getAccount1());
        Account account2 = accountService.findAccountById(transferMoney.getAccount2());
        double senderBalance = account1.getBalance();
        if(senderBalance<transferMoney.getAmount())
            throw new Exception("Insufficient Balance");
        account1.setBalance(account1.getBalance()-transferMoney.getAmount());
        account2.setBalance(account2.getBalance()+transferMoney.getAmount());
        Timestamp current = new Timestamp(System.currentTimeMillis());
        account1.setExecutionTime(current);
        account2.setExecutionTime(current);
        accountService.save(account1);
        accountService.save(account2);
        accountHistoryService.save(new AccountHistory(-1,account1.getId(),account1.getAcct_type(),account1.getBalance(),current,account1.getCustomer()));
        accountHistoryService.save(new AccountHistory(-1,account2.getId(),account2.getAcct_type(),account2.getBalance(),current,account2.getCustomer()));
        return new ResponseEntity<>(account2,HttpStatus.OK);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/getAccountStatement")
    public ResponseEntity<List<AccountHistory>> getAccountStatement(@RequestParam("id") long id, @RequestParam("acct_type") String acct_type) throws Exception {
        Account account = accountService.findAccountById(id);
        List<AccountHistory> allByAcctId = accountHistoryService.findAllByAcctId(id);
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
        Iterable<Account> accounts = accountService.findAllAccounts();
        for (Account account: accounts) {
            double amount = 1.035*account.getBalance();
            account.setBalance(amount);
            Timestamp current = new Timestamp(System.currentTimeMillis());
            account.setExecutionTime(current);
            accountService.save(account);
            accountHistoryService.save(new AccountHistory(-1,account.getId(),account.getAcct_type(),amount,account.getExecutionTime(),account.getCustomer()));
        }
        return ResponseEntity.ok("Calculated Interest");
    }

}
