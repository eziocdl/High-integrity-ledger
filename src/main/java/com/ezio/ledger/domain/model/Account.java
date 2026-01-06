package com.ezio.ledger.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Account {

  private final UUID id;
  private Money balance;

  public Account(UUID id) {
    this.id = Objects.requireNonNull(id, "Account ID must not be null");
    this.balance = Money.zero();
  }

  public void credit(Money amount) {
    Objects.requireNonNull(amount, "Credit amount must not be null");
    if (amount.isNegative()) {
      throw new IllegalArgumentException("Credit amount must be positive");
    }

    this.balance = this.balance.add(amount);
  }

  public void debit(Money amount) {
    Objects.requireNonNull(amount, "Debit amount must not be null");
    if (amount.isNegative()) {
      throw new IllegalArgumentException("Debit amount must be positive");
    }

    if (amount.isGreaterThan(this.balance)) {
      throw new IllegalArgumentException("Insufficient funds for debit");
    }

    this.balance = this.balance.subtract(amount);
  }

  public Money getBalance() {
    return balance;
  }

  public UUID getId() {
    return id;
  }
}
