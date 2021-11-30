package com.pgr301.exam;

import com.pgr301.exam.model.Account;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class BankAccountApplicationTest {


    @Test
    public void MakeAccountAndGetBalance() {
        Account a = new Account();
        BigDecimal balance = new BigDecimal(4200);

        a.setBalance(balance);

        assertEquals(balance, a.getBalance());
    }
}
