package hello.jdbc.exception;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
public class RollbackTest {

	@Autowired
	RollbackService rollbackService;

	@Test
	void runtimeException() {
		assertThatThrownBy(() -> rollbackService.runtimeException())
			.isInstanceOf(RuntimeException.class);
	}

	@Test
	void checkedException() {
		assertThatThrownBy(() -> rollbackService.checkedException())
			.isInstanceOf(MyException.class);
	}

	@Test
	void rollbackFor() {
		assertThatThrownBy(() -> rollbackService.rollbackFor())
			.isInstanceOf(MyException.class);
	}

	@TestConfiguration
	static class TestConfig {
		@Bean
		RollbackService rollbackService() {
			return new RollbackService();
		}
	}

	@Slf4j
	static class RollbackService {

		// unchecked exception : 롤백
		@Transactional
		public void runtimeException() {
			log.info("runtimeException");
			throw new RuntimeException("runtimeException");
		}

		// checked exception : 커밋
		@Transactional
		public void checkedException() throws MyException {
			log.info("checkedException");
			throw new MyException();
		}

		@Transactional(rollbackFor = MyException.class)
		public void rollbackFor() throws MyException {
			log.info("rollbackFor");
			throw new MyException();
		}
	}

	static class MyException extends Exception {

	}

}
