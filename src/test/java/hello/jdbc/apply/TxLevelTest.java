package hello.jdbc.apply;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.extern.slf4j.Slf4j;

/*
 * Tx 설정 적용 level
 */
@Slf4j
@SpringBootTest
public class TxLevelTest {

	@Autowired
	LevelService levelService;

	@Test
	void txTest() throws Exception {
		levelService.write();
		levelService.read();
	}

	@TestConfiguration
	static class TestConfig {
		@Bean
		LevelService levelService() {
			return new LevelService();
		}
	}

	@Slf4j
	@Transactional(readOnly = true)
	static class LevelService {

		@Transactional(readOnly = false)
		public void write() {
			log.info("call write");
			printTxInfo();
		}

		public void read() {
			log.info("call read");
			printTxInfo();
		}

		private void printTxInfo() {
			final boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
			final boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

			log.info("txActive: {}, txReadOnly: {} ", txActive, isReadOnly);

		}
	}

}
