package com.ezio.ledger.infra.controller;

import com.ezio.ledger.accounting.application.dto.TransferCommand;
import com.ezio.ledger.accounting.application.usecase.TransferFundsUseCase;
import com.ezio.ledger.accounting.infra.controller.TransferController;
import com.ezio.ledger.accounting.infra.controller.TransferRequest;
import com.ezio.ledger.accounting.infra.metrics.TransferMetrics;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
@AutoConfigureJson
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransferFundsUseCase useCase;

    @MockitoBean
    private TransferMetrics metrics;

    @Test
    @DisplayName("Return 200 when transfer is successful")
    void shouldReturn200WhenTransferIsSuccessful() throws Exception {
        UUID from = UUID.randomUUID();
        UUID to = UUID.randomUUID();

        doAnswer(invocation -> {
            Runnable action = invocation.getArgument(0);
            action.run();
            return null;
        }).when(metrics).recordTransfer(any(Runnable.class));

        doNothing().when(useCase).execute(any(TransferCommand.class));

        var request = new TransferRequest(from, to, new BigDecimal("100.00"), "BRL");

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("Return 422 when insufficient funds")
    void shouldReturn422WhenInsufficientFunds() throws Exception {
        UUID from = UUID.randomUUID();
        UUID to = UUID.randomUUID();
        doAnswer(invocation -> {
            Runnable action = invocation.getArgument(0);
            action.run();
            return null;
        }).when(metrics).recordTransfer(any(Runnable.class));

        doThrow(new IllegalArgumentException("Insufficient funds"))
                .when(useCase).execute(any(TransferCommand.class));

        var request = new TransferRequest(from, to, new BigDecimal("500.00"), "BRL");

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("INSUFFICIENT_FUNDS"));
    }
}