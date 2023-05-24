package hello.jdbc.apply;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.extern.slf4j.Slf4j;

/**
 * 내부 호출시 트랜잭션 적용 여부
 */
@Slf4j
@SpringBootTest
public class InternalCallV1Test {

	@Autowired
	CallService callService;

	@Test
	void printProxy() throws Exception {
		log.info("callService class = {}", callService.getClass());
	}

	@Test
	void internalCall() throws Exception {
		callService.internal();
	}

	@Test
	void externalCall() throws Exception {
		callService.external();
	}

	@TestConfiguration
	static class TestConfig {
		@Bean
		CallService callService() {
			return new CallService();
		}
	}

	static class CallService {
		public void external() {
			log.info("call external");
			printTxInfo();
			internal();
		}

		@Transactional
		public void internal() {
			log.info("call internal");
			printTxInfo();
		}

		private void printTxInfo() {
			final boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
			final boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

			log.info("txActive: {}, txReadOnly: {} ", txActive, isReadOnly);

		}
	}

}
