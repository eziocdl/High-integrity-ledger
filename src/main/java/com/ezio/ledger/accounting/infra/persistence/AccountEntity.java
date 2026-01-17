package com.ezio.ledger.accounting.infra.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "account") // Mapeia para a tabela criada no V1__create_account_table.sql
public class AccountEntity {

    @Id
    private UUID id;

    @Column(name = "balance_amount", nullable = false)
    private BigDecimal balanceAmount;

    @Column(name = "balance_currency", nullable = false, length = 3)
    private String balanceCurrency;

    @Version // O campo version existe no seu SQL V1
    private Long version;

    protected AccountEntity() {}

    public AccountEntity(UUID id, BigDecimal balanceAmount, String balanceCurrency) {
        this.id = id;
        this.balanceAmount = balanceAmount;
        this.balanceCurrency = balanceCurrency;
    }

    public UUID getId() { return id; }
    public BigDecimal getBalanceAmount() { return balanceAmount; }
    public String getBalanceCurrency() { return balanceCurrency; }


    public Long getVersion() { return version; }

    public void setBalanceAmount(BigDecimal balanceAmount) {
        this.balanceAmount = balanceAmount;
    }
}