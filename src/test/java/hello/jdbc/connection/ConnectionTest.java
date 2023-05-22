package hello.jdbc.connection;

import static hello.jdbc.connection.ConnectionConst.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.zaxxer.hikari.HikariDataSource;

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

	@Test
	void dataSourceDriverManager() throws SQLException {
		final DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
		userDataSource(dataSource);
	}

	@Test
	void dataSourceConnectionPool() throws SQLException, InterruptedException {
		final HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(URL);
		dataSource.setUsername(USERNAME);
		dataSource.setPassword(PASSWORD);
		dataSource.setMaximumPoolSize(10);
		dataSource.setPoolName("MyPool");

		userDataSource(dataSource);
		Thread.sleep(1000);

	}

	private void userDataSource(DataSource dataSource) throws SQLException {
		final Connection connection1 = dataSource.getConnection();
		final Connection connection2 = dataSource.getConnection();
		log.info("get Connection={}, class={}", connection1, connection1.getClass());
		log.info("get Connection={}, class={}", connection2, connection2.getClass());

	}
}
