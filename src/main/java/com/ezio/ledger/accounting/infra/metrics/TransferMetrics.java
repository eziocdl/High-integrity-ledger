package com.ezio.ledger.accounting.infra.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;


@Component
public class TransferMetrics {

    private final Counter transferCounter;
    private final Timer transferTimer;
    private final Counter failedTransferCounter;

    public TransferMetrics(MeterRegistry registry) {
        this.transferCounter = Counter.builder("transfer.count")
                .description("Total number of transfers")
                .register(registry);

        this.transferTimer = Timer.builder("transfer.duration")
                .description("Duration of transfers")
                .register(registry);
        this.failedTransferCounter = Counter.builder("transfers.failed")
                .description("Total number of failed transfers")
                .register(registry);

    }

    public void recordTransfer(Runnable operation) {

        try {
            transferTimer.record(() -> {
                operation.run();
                transferCounter.increment();
            });
        } catch (Exception e) {
            failedTransferCounter.increment();
            throw e;
        }

    }
}


