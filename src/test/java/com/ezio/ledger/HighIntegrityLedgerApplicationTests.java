package com.ezio.ledger;

import com.ezio.ledger.shared.infra.TestContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestContainerConfig.class)
@SpringBootTest
class HighIntegrityLedgerApplicationTests {

}
