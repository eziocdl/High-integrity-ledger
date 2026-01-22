package com.ezio.ledger.accounting.application.usecase;

import com.ezio.ledger.accounting.application.dto.TransferCommand;
import com.ezio.ledger.accounting.domain.gateway.AccountRepository;
import com.ezio.ledger.accounting.domain.model.Account;
import com.ezio.ledger.shared.domain.Money;
import com.ezio.ledger.shared.infra.TestContainerConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestContainerConfig.class)
class TransferFundsUseCaseIntegrationTest {

    @Autowired
    private TransferFundsUseCase useCase;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DisplayName("Should make rollback transaction if balance is insufficient")
    void shouldRollbackTransactionIfBalanceIsInsufficient() {
        // 1. Setup
        UUID aliceId = UUID.randomUUID();
        UUID bobId = UUID.randomUUID();

        Account alice = new Account(aliceId);
        alice.credit(Money.of(new BigDecimal("100.00"), "BRL"));
        accountRepository.save(alice);

        // Criando Bob com 0
        Account bob = new Account(bobId);
        accountRepository.save(bob);

        TransferCommand command = new TransferCommand(
                aliceId,
                bobId,
                new BigDecimal("150.00"),
                "BRL"
        );

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));

        Account aliceAfter = accountRepository.findById(aliceId).orElseThrow();
        Account bobAfter = accountRepository.findById(bobId).orElseThrow();
        assertEquals(0, new BigDecimal("100.00").compareTo(aliceAfter.getBalance().amount()));
        assertEquals(0, BigDecimal.ZERO.compareTo(bobAfter.getBalance().amount()));
    }
}
