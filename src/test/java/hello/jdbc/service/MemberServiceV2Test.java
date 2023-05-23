package hello.jdbc.service;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;

class MemberServiceV2Test {

	public static final String MEMBER_A = "memberA";
	public static final String MEMBER_B = "memberB";
	public static final String MEMBER_EX = "ex";

	private MemberRepositoryV2 memberRepository;
	private MemberServiceV2 memberService;

	@BeforeEach
	void before() {
		final DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
		memberRepository = new MemberRepositoryV2(dataSource);
		memberService = new MemberServiceV2(dataSource, memberRepository);
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

}