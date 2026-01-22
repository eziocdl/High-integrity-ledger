package com.ezio.ledger.accounting.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransferResponse(
        UUID transactionId,
        String status,
        LocalDateTime timestamp
) {
}
