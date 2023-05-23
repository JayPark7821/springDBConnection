package hello.jdbc.service;

import java.sql.SQLException;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;

public class MemberServiceV3_2 {

	// private final DataSource dataSource;
	// private final PlatformTransactionManager transactionManager;
	private final MemberRepositoryV3 memberRepository;
	private final TransactionTemplate txTemplate;

	public MemberServiceV3_2(final PlatformTransactionManager transactionManager,
		final MemberRepositoryV3 memberRepository
	) {
		this.memberRepository = memberRepository;
		this.txTemplate = new TransactionTemplate(transactionManager);
	}

	public void accountTransfer(String fromId, String toId, int money) throws SQLException {
		txTemplate.executeWithoutResult(status -> {
			try {
				bizLogic(fromId, toId, money);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
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
