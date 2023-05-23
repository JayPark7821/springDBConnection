package hello.jdbc.service;

import static org.assertj.core.api.Assertions.*;

import java.sql.SQLException;
import java.util.Arrays;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class MemberServiceV3_4Test {

	public static final String MEMBER_A = "memberA";
	public static final String MEMBER_B = "memberB";
	public static final String MEMBER_EX = "ex";

	@Autowired
	private MemberRepositoryV3 memberRepository;
	@Autowired
	private MemberServiceV3_3 memberService;

	@TestConfiguration
	static class TestConfig {
		private final DataSource dataSource;

		public TestConfig(final DataSource dataSource) {
			this.dataSource = dataSource;
		}

		@Bean
		MemberRepositoryV3 memberRepositoryV3() {
			return new MemberRepositoryV3(dataSource);
		}

		@Bean
		MemberServiceV3_3 memberServiceV3_3() {
			return new MemberServiceV3_3(memberRepositoryV3());
		}
	}

	@AfterEach
	void afterEach() throws SQLException {
		memberRepository.delete(MEMBER_EX);
		memberRepository.delete(MEMBER_A);
		memberRepository.delete(MEMBER_B);
	}

	@Test
	@DisplayName("정상 이체")
	void accountTransfer() throws Exception {
		final Member memberA = new Member(MEMBER_A, 10000);
		final Member memberB = new Member(MEMBER_B, 10000);
		memberRepository.save(memberA);
		memberRepository.save(memberB);

		memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 1000);

		final Member findMemberA = memberRepository.findById(memberA.getMemberId());
		final Member findMemberB = memberRepository.findById(memberB.getMemberId());
		assertThat(findMemberA.getMoney()).isEqualTo(9000);
		assertThat(findMemberB.getMoney()).isEqualTo(11000);
	}

	@Test
	@DisplayName("이체중 예외")
	void accountTransferEx() throws Exception {
		final Member memberA = new Member(MEMBER_A, 10000);
		final Member ex = new Member(MEMBER_EX, 10000);
		memberRepository.save(memberA);
		memberRepository.save(ex);

		assertThatThrownBy(
			() -> memberService.accountTransfer(memberA.getMemberId(), ex.getMemberId(), 1000)).isInstanceOf(
			IllegalStateException.class);

		final Member findMemberA = memberRepository.findById(memberA.getMemberId());
		final Member findMemberEX = memberRepository.findById(ex.getMemberId());
		assertThat(findMemberA.getMoney()).isEqualTo(10000);
		assertThat(findMemberEX.getMoney()).isEqualTo(10000);
	}

	@Test
	void aopCheck() throws Exception {
		log.info("memberService ={}", memberService.getClass());
		Arrays.stream(memberService.getClass().getDeclaredMethods()).forEach(method -> {
			log.info("method = {}", method.getName());
		});
	}

}