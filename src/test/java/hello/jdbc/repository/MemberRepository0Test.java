package hello.jdbc.repository;

import static org.assertj.core.api.Assertions.*;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class MemberRepository0Test {

	MemberRepository0 repository0 = new MemberRepository0();

	@Test
	void crud() throws SQLException {
		final Member member = new Member("memberV111", 10000);
		repository0.save(member);

		final Member selectedMember = repository0.findById(member.getMemberId());
		log.info("selected member = {}", selectedMember);
		assertThat(selectedMember).isEqualTo(member);

		// update money: 10000 -> 20000
		final int updateMoney = 20000;
		repository0.update(member.getMemberId(), updateMoney);
		final Member updatedMember = repository0.findById(member.getMemberId());
		assertThat(updatedMember.getMoney()).isEqualTo(updateMoney);

		// delete member
		repository0.delete(member.getMemberId());
		assertThatThrownBy(() -> repository0.findById(member.getMemberId()))
			.isInstanceOf(NoSuchElementException.class);
	}
}