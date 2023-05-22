package hello.jdbc.connection;

import static hello.jdbc.connection.ConnectionConst.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectionTest {

	@Test
	void driverManager() throws SQLException {
		final Connection connection1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		final Connection connection2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		log.info("get Connection={}, class={}", connection1, connection1.getClass());
		log.info("get Connection={}, class={}", connection2, connection2.getClass());
	}

	private void userDataSource(DataSource dataSource) throws SQLException {
		final Connection connection1 = dataSource.getConnection();
		final Connection connection2 = dataSource.getConnection();
		log.info("get Connection={}, class={}", connection1, connection1.getClass());
		log.info("get Connection={}, class={}", connection2, connection2.getClass());

	}
}
