package hello.jdbc.propagation;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class MemberServiceTest {

	@Autowired
	MemberService memberService;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	LogRepository logRepository;

	/**
	 * memberService 		@Transactional: OFF
	 * memberRepository 	@Transactional: ON
	 * logRepository 		@Transactional: ON
	 */
	@Test
	void outerTxOff_success() throws Exception {
		//given
		String username = "outerTxOff_success";
		//when
		memberService.joinV1(username);
		//then
		assertThat(memberRepository.find(username)).isPresent();
		assertThat(logRepository.find(username)).isPresent();

	}

	/**
	 * memberService 		@Transactional: OFF
	 * memberRepository 	@Transactional: ON
	 * logRepository 		@Transactional: ON exception
	 */
	@Test
	void outerTxOff_fail() throws Exception {
		//given
		String username = "로그예외_outerTxOff_fail";
		//when
		assertThatThrownBy(() -> memberService.joinV1(username))
			.isInstanceOf(RuntimeException.class);
		//then
		assertThat(memberRepository.find(username)).isPresent();
		assertThat(logRepository.find(username)).isEmpty();

	}

	/**
	 * memberService 		@Transactional: ON
	 * memberRepository 	@Transactional: OFF
	 * logRepository 		@Transactional: OFF
	 */
	@Test
	void single_transaction() throws Exception {
		//given
		String username = "outerTxOff_success";
		//when
		memberService.joinV1(username);
		//then
		assertThat(memberRepository.find(username)).isPresent();
		assertThat(logRepository.find(username)).isPresent();

	}

	/**
	 * memberService 		@Transactional: ON
	 * memberRepository 	@Transactional: ON
	 * logRepository 		@Transactional: ON
	 */
	@Test
	void outerTxOn_success() throws Exception {
		//given
		String username = "outerTxOn_success";
		//when
		memberService.joinV1(username);
		//then
		assertThat(memberRepository.find(username)).isPresent();
		assertThat(logRepository.find(username)).isPresent();

	}

	/**
	 * memberService 		@Transactional: ON
	 * memberRepository 	@Transactional: ON
	 * logRepository 		@Transactional: ON Exception
	 */
	@Test
	void outerTxOn_fail() throws Exception {
		//given
		String username = "로그예외_outerTxOn_success";
		//when
		assertThatThrownBy(() -> memberService.joinV1(username))
			.isInstanceOf(RuntimeException.class);
		//then
		assertThat(memberRepository.find(username)).isEmpty();
		assertThat(logRepository.find(username)).isEmpty();
	}

	/**
	 * memberService 		@Transactional: ON
	 * memberRepository 	@Transactional: ON
	 * logRepository 		@Transactional: ON Exception
	 */
	@Test
	void recoverException_fail() throws Exception {
		//given
		String username = "로그예외_recoverException_fail";
		//when
		assertThatThrownBy(() -> memberService.joinV2(username))
			.isInstanceOf(UnexpectedRollbackException.class);
		//then
		assertThat(memberRepository.find(username)).isEmpty();
		assertThat(logRepository.find(username)).isEmpty();
	}

	/**
	 * memberService 		@Transactional: ON
	 * memberRepository 	@Transactional: ON
	 * logRepository 		@Transactional: ON(REQUIRES_NEW) Exception
	 */
	@Test
	void recoverException_success() throws Exception {
		//given
		String username = "로그예외_recoverException_success";
		//when
		memberService.joinV2(username);
		//then
		assertThat(memberRepository.find(username)).isPresent();
		assertThat(logRepository.find(username)).isEmpty();
	}

}