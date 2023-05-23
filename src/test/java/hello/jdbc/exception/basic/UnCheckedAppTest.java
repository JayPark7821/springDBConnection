package hello.jdbc.exception.basic;

import java.sql.SQLException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class UnCheckedAppTest {

	@Test
	void unChecked() {
		Controller controller = new Controller();

		Assertions.assertThatThrownBy(controller::request)
			.isInstanceOf(Exception.class);
	}

	static class Controller {
		Service service = new Service();

		public void request() {
			service.call();
		}
	}

	static class Service {
		Repository repository = new Repository();
		NetworkClient networkClient = new NetworkClient();

		public void call() {
			repository.call();
			networkClient.call();
		}
	}

	static class NetworkClient {

		public void call() {
			throw new RuntimeConnectException("네트워크 연결 실패");
		}
	}

	static class Repository {
		public void call() {
			try {
				runSql();
			} catch (SQLException e) {
				throw new RuntimeSQLException(e);
			}
		}

		public void runSql() throws SQLException {
			throw new SQLException("ex");
		}
	}

	static class RuntimeConnectException extends RuntimeException {
		public RuntimeConnectException(String message) {
			super(message);
		}
	}

	static class RuntimeSQLException extends RuntimeException {
		public RuntimeSQLException(final Throwable cause) {
			super(cause);
		}
	}
}
