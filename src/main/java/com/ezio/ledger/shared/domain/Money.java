package com.ezio.ledger.shared.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

public record Money(BigDecimal amount, Currency currency) implements Comparable<Money> {

    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;


    public Money {
        Objects.requireNonNull(amount, "Amount must not be null");
        Objects.requireNonNull(currency, "Currency must not be null");
        amount = amount.setScale(4, DEFAULT_ROUNDING);
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(amount, Currency.getInstance(currencyCode));
    }


    public static Money zero(String currencyCode) {
        return new Money(BigDecimal.ZERO, Currency.getInstance(currencyCode));
    }



    public static Money of(BigDecimal amount) {
        return of(amount, "BRL");
    }

    public static Money zero() {
        return zero("BRL");
    }

    public Money add(Money other) {
        checkCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        checkCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public boolean isGreaterThanOrEqualTo(Money other) {
        checkCurrency(other);
        return this.amount.compareTo(other.amount) >= 0;
    }

    private void checkCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    "Cannot operate on different currencies: " + this.currency + " vs " + other.currency);
        }
    }

    @Override
    public int compareTo(Money o) {
        checkCurrency(o);
        return this.amount.compareTo(o.amount);
    }

    @Override
    public String toString() {
        return currency.getSymbol() + " " + amount;
    }
}
