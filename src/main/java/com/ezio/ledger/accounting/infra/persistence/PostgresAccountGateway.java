package com.ezio.ledger.accounting.infra.persistence;

import com.ezio.ledger.accounting.domain.gateway.AccountRepository;
import com.ezio.ledger.accounting.domain.model.Account;
import com.ezio.ledger.shared.domain.Money;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PostgresAccountGateway implements AccountRepository {

    private final JpaAccountRepository jpaRepository;

    public PostgresAccountGateway(JpaAccountRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Account save(Account account) {

        AccountEntity entity = jpaRepository.findById(account.getId())
                .orElseGet(() -> toEntity(account));


        entity.setBalanceAmount(account.getBalance().amount());



        AccountEntity savedEntity = jpaRepository.save(entity);


        return toDomain(savedEntity);
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Account> findByIdWithLock(UUID id) {

        return jpaRepository.findByIdLocked(id).map(this::toDomain);
    }



    private AccountEntity toEntity(Account account) {
        return new AccountEntity(
                account.getId(),
                account.getBalance().amount(),
                account.getBalance().currency().getCurrencyCode()
        );
    }

    private Account toDomain(AccountEntity entity) {
        // Reconstrói o Account passando ID, Money e Version
        return Account.reconstitute(
                entity.getId(),
                Money.of(entity.getBalanceAmount(), entity.getBalanceCurrency()),
                entity.getVersion()
        );
    }
}
