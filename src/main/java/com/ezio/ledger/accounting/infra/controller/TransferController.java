package com.ezio.ledger.accounting.infra.controller;

import com.ezio.ledger.accounting.application.dto.TransferCommand;
import com.ezio.ledger.accounting.application.dto.TransferResponse;
import com.ezio.ledger.accounting.application.usecase.TransferFundsUseCase;
import com.ezio.ledger.accounting.infra.metrics.TransferMetrics;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final TransferFundsUseCase useCase;
    private final TransferMetrics metrics;

    public TransferController(TransferFundsUseCase useCase, TransferMetrics metrics) {
        this.useCase = useCase;
        this.metrics = metrics;

    }

    @PostMapping
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request) {
        var command = new TransferCommand(
                request.fromAccountId(),
                request.toAccountId(),
                request.amount(),
                request.currency()
        );

        metrics.recordTransfer(() -> useCase.execute(command));

        var response = new TransferResponse(
                UUID.randomUUID(),
                "COMPLETED",
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }
}
