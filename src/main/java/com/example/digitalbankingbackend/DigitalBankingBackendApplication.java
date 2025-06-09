package com.example.digitalbankingbackend;

import com.example.digitalbankingbackend.entities.*;
import com.example.digitalbankingbackend.enums.AccountStatus;
import com.example.digitalbankingbackend.enums.OperationType;
import com.example.digitalbankingbackend.repositories.AccountOperationRepository;
import com.example.digitalbankingbackend.repositories.BankAccountRepository;
import com.example.digitalbankingbackend.repositories.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@SpringBootApplication
public class DigitalBankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalBankingBackendApplication.class, args);
    }
    
    @Bean
    CommandLineRunner start(CustomerRepository customerRepository,
                            BankAccountRepository bankAccountRepository,
                            AccountOperationRepository accountOperationRepository) {        return args -> {
            // Create Customers with historical creation dates
            Calendar cal = Calendar.getInstance();
            String[] customerNames = {"Hassan", "Imane", "Mohamed"};
            
            for (int i = 0; i < customerNames.length; i++) {
                Customer customer = new Customer();
                customer.setName(customerNames[i]);
                customer.setEmail(customerNames[i].toLowerCase() + "@gmail.com");
                
                // Set customer creation date to historical dates (6-1 months ago)
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, -(6 - i)); // Hassan: 6 months ago, Imane: 5 months ago, Mohamed: 4 months ago
                cal.set(Calendar.DAY_OF_MONTH, (int)(Math.random() * 28) + 1); // Random day 1-28
                customer.setCreatedAt(cal.getTime());
                
                customerRepository.save(customer);            }

            // Create accounts with historical dates spread over last 6 months
            // Reuse the existing Calendar instance
            
            customerRepository.findAll().forEach(customer -> {
                // Create Current Account with historical date
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random() * 90000 + 10000); // Random balance between 10k and 100k
                
                // Set creation date to a random day in a past month
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, -(int)(Math.random() * 6)); // 0-5 months ago
                cal.set(Calendar.DAY_OF_MONTH, (int)(Math.random() * 28) + 1); // Random day 1-28
                currentAccount.setCreatedAt(cal.getTime());
                
                currentAccount.setStatus(AccountStatus.ACTIVATED);
                currentAccount.setCustomer(customer);
                currentAccount.setOverDraft(5000); // Example overdraft limit
                bankAccountRepository.save(currentAccount);

                // Create a Saving Account with different historical date
                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random() * 120000 + 10000); // Random balance between 10k and 130k
                
                // Set different creation date
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, -(int)(Math.random() * 5)); // 0-4 months ago
                cal.set(Calendar.DAY_OF_MONTH, (int)(Math.random() * 28) + 1); // Random day 1-28
                savingAccount.setCreatedAt(cal.getTime());
                
                savingAccount.setStatus(AccountStatus.ACTIVATED);
                savingAccount.setCustomer(customer);
                savingAccount.setInterestRate(5.5); // Example interest rate
                bankAccountRepository.save(savingAccount);
            });

            // Create additional accounts for better trend visualization
            Customer firstCustomer = customerRepository.findAll().get(0);
            
            // Create 2 more accounts from 2-3 months ago to show growth
            for (int i = 0; i < 2; i++) {
                CurrentAccount additionalAccount = new CurrentAccount();
                additionalAccount.setId(UUID.randomUUID().toString());
                additionalAccount.setBalance(Math.random() * 50000 + 5000);
                
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, -2 - i); // 2-3 months ago
                cal.set(Calendar.DAY_OF_MONTH, (int)(Math.random() * 28) + 1);
                additionalAccount.setCreatedAt(cal.getTime());
                
                additionalAccount.setStatus(AccountStatus.ACTIVATED);
                additionalAccount.setCustomer(firstCustomer);
                additionalAccount.setOverDraft(3000);
                bankAccountRepository.save(additionalAccount);
            }

            // Create Operations for each account with historical dates
            bankAccountRepository.findAll().forEach(account -> {
                for (int i = 0; i < 5; i++) { // Create 5 operations per account
                    // Credit Operation
                    AccountOperation creditOp = new AccountOperation();
                    
                    // Set operation date after account creation date
                    cal.setTime(account.getCreatedAt());
                    cal.add(Calendar.DAY_OF_MONTH, (int)(Math.random() * 30) + 1); // 1-30 days after creation
                    creditOp.setOperationDate(cal.getTime());
                    
                    creditOp.setAmount(Math.random() * 10000 + 1000); // Random amount between 1k and 11k
                    creditOp.setType(OperationType.CREDIT);
                    creditOp.setBankAccount(account);
                    creditOp.setDescription("Credit Operation");
                    accountOperationRepository.save(creditOp);

                    // Debit Operation
                    AccountOperation debitOp = new AccountOperation();
                    
                    cal.setTime(account.getCreatedAt());
                    cal.add(Calendar.DAY_OF_MONTH, (int)(Math.random() * 45) + 5); // 5-45 days after creation
                    debitOp.setOperationDate(cal.getTime());
                    
                    debitOp.setAmount(Math.random() * 5000 + 500); // Random amount between 500 and 5500
                    debitOp.setType(OperationType.DEBIT);
                    debitOp.setBankAccount(account);
                    debitOp.setDescription("Debit Operation");
                    accountOperationRepository.save(debitOp);
                }
            });

            // Fetch and print some data
            System.out.println("--- Customers ---");
            customerRepository.findAll().forEach(System.out::println);

            System.out.println("\n--- Bank Accounts ---");
            bankAccountRepository.findAll().forEach(acc -> {
                System.out.println(acc.getClass().getSimpleName() + ":" + acc.getId() + " Balance:" + acc.getBalance() + " Customer:" + acc.getCustomer().getName());
            });

            System.out.println("\n--- Account Operations (First Account) ---");
            if (!bankAccountRepository.findAll().isEmpty()) {
                String firstAccountId = bankAccountRepository.findAll().get(0).getId();
                accountOperationRepository.findByBankAccountId(firstAccountId).forEach(op -> {
                    System.out.println(op.getOperationDate() + " " + op.getType() + " Amount:" + op.getAmount());
                });
            }
        };
    }
}