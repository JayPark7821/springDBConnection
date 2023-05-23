package hello.jdbc.service;

import java.sql.SQLException;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberServiceV3_1 {

	// private final DataSource dataSource;
	private final PlatformTransactionManager transactionManager;
	private final MemberRepositoryV3 memberRepository;

	public void accountTransfer(String fromId, String toId, int money) throws SQLException {
		final TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

		try {
			bizLogic(fromId, toId, money);
			transactionManager.commit(status);
		} catch (Exception e) {
			transactionManager.rollback(status);
			throw new IllegalStateException(e);
		}

	}

	private void bizLogic(final String fromId, final String toId, final int money) throws
		SQLException {
		final Member fromMember = memberRepository.findById(fromId);
		final Member toMember = memberRepository.findById(toId);

		memberRepository.update(fromId, fromMember.getMoney() - money);
		validation(toMember);
		memberRepository.update(toId, toMember.getMoney() + money);
	}

	private static void validation(final Member toMember) {
		if (toMember.getMemberId().equals("ex")) {
			throw new IllegalStateException("예외 발생");
		}
	}
}
