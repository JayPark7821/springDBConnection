package hello.jdbc.propagation;

import static org.assertj.core.api.Assertions.*;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class BasicTxTest {

	@Autowired
	PlatformTransactionManager txManager;

	@TestConfiguration
	static class Config {
		@Bean
		public PlatformTransactionManager transactionManager(DataSource dataSource) {
			return new DataSourceTransactionManager(dataSource);
		}
	}

	@Test
	void commit() throws Exception {
		log.info("트랜잭션 시작");
		final TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());
		log.info("트랜잭션 커밋 시작");
		txManager.commit(status);
		log.info("트랜잭션 커밋 완료");
	}

	@Test
	void rollback() throws Exception {
		log.info("트랜잭션 시작");
		final TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());
		log.info("트랜잭션 롤백 시작");
		txManager.rollback(status);
		log.info("트랜잭션 롤백 완료");
	}

	@Test
	void double_commit() throws Exception {
		log.info("트랜잭션1 시작");
		final TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
		log.info("트랜잭션1 커밋 시작");
		txManager.commit(tx1);

		log.info("트랜잭션2 시작");
		final TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
		log.info("트랜잭션2 커밋 시작");
		txManager.commit(tx2);

	}

	@Test
	void double_commit_rollback() throws Exception {
		log.info("트랜잭션1 시작");
		final TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
		log.info("트랜잭션1 커밋 시작");
		txManager.commit(tx1);

		log.info("트랜잭션2 시작");
		final TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
		log.info("트랜잭션2 rollback 시작");
		txManager.rollback(tx2);

	}

	@Test
	void inner_commit() throws Exception {
		log.info("외부 트랜잭션 시작");
		final TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
		log.info("outer.isNewTransaction()= {}", outer.isNewTransaction());

		log.info("내부 트랜잭션 시작");
		final TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
		log.info("inner.isNewTransaction()= {}", inner.isNewTransaction());
		log.info("내부 트랜잭션 커밋");
		txManager.commit(inner);

		log.info("외부 트랜잭션 커밋");
		txManager.commit(outer);

	}

	@Test
	void outer_rollback() throws Exception {
		log.info("외부 트랜잭션 시작");
		final TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());

		log.info("내부 트랜잭션 시작");
		final TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
		log.info("내부 트랜잭션 커밋");
		txManager.commit(inner);

		log.info("외부 트랜잭션 롤백");
		txManager.rollback(outer);
	}

	@Test
	void inner_rollback() throws Exception {
		log.info("외부 트랜잭션 시작");
		final TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());

		log.info("내부 트랜잭션 시작");
		final TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
		log.info("내부 트랜잭션 롤백");
		txManager.rollback(inner);
		// Participating transaction failed - marking existing transaction as rollback-only
		// Setting JDBC transaction [HikariProxyConnection@709955086 wrapping conn0: url=jdbc:h2:tcp://localhost/~/test user=SA] rollback-only

		log.info("외부 트랜잭션 커밋");
		assertThatThrownBy(() -> txManager.commit(outer))
			.isInstanceOf(UnexpectedRollbackException.class);
	}

	@Test
	void inner_rollback_requires_new() {
		log.info("외부 트랜잭션 시작");
		final TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
		log.info("outer.isNewTransaction()= {}", outer.isNewTransaction());

		log.info("내부 트랜잭션 시작");
		final DefaultTransactionAttribute definition = new DefaultTransactionAttribute();
		definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		final TransactionStatus inner = txManager.getTransaction(definition);
		//Suspending current transaction, creating new transaction with name [null]
		log.info("inner.isNewTransaction()= {}", inner.isNewTransaction());

		log.info("내부 트랜잭션 롤백");
		txManager.rollback(inner);

		log.info("외부 트랜잭션 커밋");
		txManager.commit(outer);

	}
}
