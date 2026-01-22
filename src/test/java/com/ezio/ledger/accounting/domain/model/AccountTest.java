package com.ezio.ledger.accounting.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.ezio.ledger.shared.domain.Money;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Unit Test: Account Aggregate")
class AccountTest {

  @Test
  @DisplayName("Should initialize account with zero balance")
  void shouldInitializeWithZeroBalance() {
    var accountId = UUID.randomUUID();
    var account = new Account(accountId);

    assertEquals(Money.zero(), account.getBalance());
    assertEquals(accountId, account.getId());
  }

  @Test
  @DisplayName("Should credit amount to account")
  void shouldCreditAmount() {
    var account = new Account(UUID.randomUUID());
    var depositAmount = Money.of(new BigDecimal("100.00"));

    account.credit(depositAmount);

    assertEquals(depositAmount, account.getBalance());
  }

  @Test
  @DisplayName("Should debit amount from account when balance is sufficient")
  void shouldDebitAmount() {
    var account = new Account(UUID.randomUUID());

    account.credit(Money.of(new BigDecimal("100.00")));

    var withdrawAmount = Money.of(new BigDecimal("40.00"));
    account.debit(withdrawAmount);

    var expectedBalance = Money.of(new BigDecimal("60.00"));
    assertEquals(expectedBalance, account.getBalance());
  }

  @Test
  @DisplayName("Should throw exception when balance is insufficient (Invariant Check)")
  void shouldThrowExceptionWhenInsufficientFunds() {
    var account = new Account(UUID.randomUUID());
    account.credit(Money.of(new BigDecimal("50.00")));

    var withdrawAmount = Money.of(new BigDecimal("100.00"));

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          account.debit(withdrawAmount);
        });
  }
}
