package com.ezio.ledger;

import org.springframework.boot.SpringApplication;

public class TestHighIntegrityLedgerApplication {

  public static void main(String[] args) {
    SpringApplication.from(HighIntegrityLedgerApplication::main)
        .with(TestcontainersConfiguration.class)
        .run(args);
  }
}
