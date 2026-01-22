package com.ezio.ledger;

import com.ezio.ledger.shared.infra.TestContainerConfig;
import org.springframework.boot.SpringApplication;

public class TestHighIntegrityLedgerApplication {

  public static void main(String[] args) {
    SpringApplication.from(HighIntegrityLedgerApplication::main)
        .with(TestContainerConfig.class)
        .run(args);
  }
}
