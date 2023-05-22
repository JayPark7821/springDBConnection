package hello.jdbc.repository;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.zaxxer.hikari.HikariDataSource;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class MemberRepositoryV1Test {

	MemberRepository1 repository;

	@BeforeEach
	void beforeEach() {
		// 기본 DriverManagerDataSource 사용 - 항상 새로운 커넥션 획득
		// DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

		// 커넥션 풀링
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(URL);
		dataSource.setUsername(USERNAME);
		dataSource.setPassword(PASSWORD);

		repository = new MemberRepository1(dataSource);
	}

	@Test
	void crud() throws SQLException {
		final Member member = new Member("memberV111", 10000);
		repository.save(member);

		final Member selectedMember = repository.findById(member.getMemberId());
		log.info("selected member = {}", selectedMember);
		assertThat(selectedMember).isEqualTo(member);

		// update money: 10000 -> 20000
		final int updateMoney = 20000;
		repository.update(member.getMemberId(), updateMoney);
		final Member updatedMember = repository.findById(member.getMemberId());
		assertThat(updatedMember.getMoney()).isEqualTo(updateMoney);

		// delete member
		repository.delete(member.getMemberId());
		assertThatThrownBy(() -> repository.findById(member.getMemberId()))
			.isInstanceOf(NoSuchElementException.class);
	}
}