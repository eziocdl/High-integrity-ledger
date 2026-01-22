package com.ezio.ledger.accounting.domain.model;

import com.ezio.ledger.shared.domain.Money;
import lombok.Getter;
import java.util.UUID;


@Getter
public class Account {

    private final UUID id;
    private Money balance;
    private Long version;


    public Account(UUID id) {
        this.id = id;
        this.balance = Money.zero("BRL");
    }


    private Account(UUID id, Money balance, Long version) {
        this.id = id;
        this.balance = balance;
        this.version = version;
    }

    // Factory Method: O Gateway usa isso para "hidratar" o objeto sem acionar regras de negócio (como criar conta com saldo zero)
    public static Account reconstitute(UUID id, Money balance, Long version) {
        return new Account(id, balance, version);
    }



    public void credit(Money amount) {
        this.balance = this.balance.add(amount);
    }

    public void debit(Money amount) {
        if (!this.balance.isGreaterThanOrEqualTo(amount)) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        this.balance = this.balance.subtract(amount);
    }
}
