package hello.jdbc.service;

import org.springframework.transaction.annotation.Transactional;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;

public class MemberServiceV4 {

	private final MemberRepository memberRepository;

	public MemberServiceV4(final MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Transactional
	public void accountTransfer(String fromId, String toId, int money) {
		bizLogic(fromId, toId, money);
	}

	private void bizLogic(final String fromId, final String toId, final int money) {
		final Member fromMember = memberRepository.findById(fromId);
		final Member toMember = memberRepository.findById(toId);

		memberRepository.update(fromId, fromMember.getMoney() - money);
		validation(toMember);
		memberRepository.update(toId, toMember.getMoney() + money);
	}

	public void test() {
		System.out.println("test");
	}

	private static void validation(final Member toMember) {
		if (toMember.getMemberId().equals("ex")) {
			throw new IllegalStateException("예외 발생");
		}
	}
}
