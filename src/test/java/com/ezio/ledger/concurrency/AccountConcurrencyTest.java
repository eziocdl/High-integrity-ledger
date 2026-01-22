package com.ezio.ledger.concurrency;

import com.ezio.ledger.accounting.domain.gateway.AccountRepository;
import com.ezio.ledger.accounting.domain.model.Account;
import com.ezio.ledger.shared.domain.Money;
import com.ezio.ledger.shared.infra.TestContainerConfig;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@Import(TestContainerConfig.class)
public class AccountConcurrencyTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setupDatabase() {
        System.out.println(">>> STARTING MANUAL FLYWAY MIGRATION...");
        Flyway.configure()
                .dataSource(dataSource)
                .locations("filesystem:src/main/resources/db/migration")
                .baselineOnMigrate(true)
                .load()
                .migrate();
        System.out.println(">>> TABLES CREATED SUCCESSFULLY!");
    }

    @Test
    public void shouldEnsureIntegrityDuringConcurrentWithdrawals() throws InterruptedException {
        UUID accountId = UUID.randomUUID();

        // Setup initial balance
        transactionTemplate.execute(status -> {
            Account initialAccount = new Account(accountId);
            initialAccount.credit(Money.of(new BigDecimal("1000.00"), "BRL"));
            return accountRepository.save(initialAccount);
        });

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    transactionTemplate.execute(status -> {
                        performWithdrawalWithLock(accountId);
                        return null;
                    });
                } catch (Exception e) {
                    System.err.println("Withdrawal failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Account finalAccount = accountRepository.findById(accountId).orElseThrow();
        BigDecimal finalBalance = finalAccount.getBalance().amount();

        System.out.println("Final Account Balance: " + finalBalance);
        Assertions.assertEquals(0, finalBalance.compareTo(BigDecimal.ZERO), "Balance should be zero!");
    }

    private void performWithdrawalWithLock(UUID id) {

        Account account = accountRepository.findByIdWithLock(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.debit(Money.of(new BigDecimal("100.00"), "BRL"));
        accountRepository.save(account);
    }
}
