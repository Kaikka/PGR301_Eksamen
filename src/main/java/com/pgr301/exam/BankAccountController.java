package com.pgr301.exam;

import com.pgr301.exam.model.Account;
import com.pgr301.exam.model.Transaction;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static java.util.Optional.ofNullable;

@RestController
public class BankAccountController implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private BankingCoreSystmeService bankService;

    @Autowired
    public MeterRegistry meterRegistry;

    public Logger logger = LoggerFactory.getLogger(BankAccountController.class);

    @Autowired
    public BankAccountController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Timed("back_end_exception")
    @Timed("post_transfer_delay")
    @PostMapping(path = "/account/{fromAccount}/transfer/{toAccount}", consumes = "application/json", produces = "application/json")
    public void transfer(@RequestBody Transaction tx, @PathVariable String fromAccount, @PathVariable String toAccount) {
        logger.info("Destination account balance before transfer: " + bankService.getAccount(toAccount).getBalance());
        meterRegistry.counter("transfer", "amount", String.valueOf(tx.getAmount()) ).increment();
        bankService.transfer(tx, fromAccount, toAccount);
        logger.info("Destination account balance after transfer: " + bankService.getAccount(toAccount).getBalance());
    }

    @Timed("back_end_exception")
    @Timed("post_new_account_delay")
    @PostMapping(path = "/account", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Account> updateAccount(@RequestBody Account a) {
        meterRegistry.counter("update_account").increment();
        bankService.updateAccount(a);
        return new ResponseEntity<>(a, HttpStatus.OK);
    }

    @Timed("back_end_exception")
    @Timed("get_account_by_id_delay")
    @GetMapping(path = "/account/{accountId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Account> balance(@PathVariable String accountId) {
        meterRegistry.counter("balance").increment();

        Account account = ofNullable(bankService.getAccount(accountId)).orElseThrow(AccountNotFoundException::new);
        meterRegistry.gauge("aaaccount_balance", account.getBalance());
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        logger.info("Application started");
        meterRegistry.counter("application_started").increment();
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "video not found")
    public static class AccountNotFoundException extends RuntimeException {
    }
}

