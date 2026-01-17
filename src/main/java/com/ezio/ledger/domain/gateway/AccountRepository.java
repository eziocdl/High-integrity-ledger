package com.ezio.ledger.domain.gateway;

import com.ezio.ledger.domain.model.Account;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    Account save(Account account);
    Optional<Account> findById(UUID id);
    Optional<Account> findByIdWithLock(UUID id);
}