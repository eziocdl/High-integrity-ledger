package com.ezio.ledger.accounting.application.usecase;

import com.ezio.ledger.accounting.application.dto.TransferCommand;
import com.ezio.ledger.accounting.domain.gateway.AccountRepository;
import com.ezio.ledger.accounting.domain.model.Account;
import com.ezio.ledger.shared.domain.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TransferFundsUseCase {

    private final AccountRepository accountRepository;

    public TransferFundsUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;

    }

    @Transactional
    public void execute (TransferCommand command) {
        // ... (validações iniciais e lógica de Locking continuam iguais) ...
        if(command.fromAccountId().equals(command.toAccountId())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        // Locking strategy (Mantenha isso, está ótimo!)
        UUID firstLock = command.fromAccountId().compareTo(command.toAccountId()) < 0
                ? command.fromAccountId() : command.toAccountId();
        UUID secondLock = command.fromAccountId().compareTo(command.toAccountId()) < 0
                ? command.toAccountId() : command.fromAccountId(); // Pequena correção: lógica de locking deve garantir ordem sempre

        Account source = accountRepository.findByIdWithLock(command.fromAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

        Account target = accountRepository.findByIdWithLock(command.toAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Target Account not found"));

        Money amount = Money.of(command.amount(), command.currency());

        source.debit(amount);
        target.credit(amount);

        accountRepository.save(source);
        accountRepository.save(target);
    }
}
