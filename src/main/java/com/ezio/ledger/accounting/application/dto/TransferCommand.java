package com.ezio.ledger.accounting.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferCommand(
        UUID fromAccountId,
        UUID toAccountId,
        BigDecimal amount,
        String currency

) { }
