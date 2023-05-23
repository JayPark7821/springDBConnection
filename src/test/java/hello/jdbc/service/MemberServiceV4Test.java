package hello.jdbc.service;

import static org.assertj.core.api.Assertions.*;

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
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.MemberRepositoryV5;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class MemberServiceV4Test {

	public static final String MEMBER_A = "memberA";
	public static final String MEMBER_B = "memberB";
	public static final String MEMBER_EX = "ex";

	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private MemberServiceV4 memberService;

	@TestConfiguration
	static class TestConfig {
		private final DataSource dataSource;

		public TestConfig(final DataSource dataSource) {
			this.dataSource = dataSource;
		}

		@Bean
		MemberRepository memberRepository() {
			return new MemberRepositoryV5(dataSource);
		}

		@Bean
		MemberServiceV4 memberServiceV4() {
			return new MemberServiceV4(memberRepository());
		}
	}

	@AfterEach
	void afterEach() {
		memberRepository.delete(MEMBER_EX);
		memberRepository.delete(MEMBER_A);
		memberRepository.delete(MEMBER_B);
	}

	@Test
	@DisplayName("정상 이체")
	void accountTransfer() {
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
	void accountTransferEx() {
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
	void aopCheck() {
		log.info("memberService ={}", memberService.getClass());
		Arrays.stream(memberService.getClass().getDeclaredMethods()).forEach(method -> {
			log.info("method = {}", method.getName());
		});
	}

}