package hello.jdbc.service;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberServiceV2 {

	private final DataSource dataSource;
	private final MemberRepositoryV2 memberRepository;

	public void accountTransfer(String fromId, String toId, int money) throws SQLException {
		Connection con = dataSource.getConnection();
		try {
			con.setAutoCommit(false);
			bizLogic(con, fromId, toId, money);
			con.commit();
		} catch (Exception e) {
			con.rollback();
			throw new IllegalStateException(e);
		} finally {
			release(con);
		}

	}

	private void bizLogic(final Connection con, final String fromId, final String toId, final int money) throws
		SQLException {
		final Member fromMember = memberRepository.findById(con, fromId);
		final Member toMember = memberRepository.findById(con, toId);

		memberRepository.update(con, fromId, fromMember.getMoney() - money);
		validation(toMember);
		memberRepository.update(con, toId, toMember.getMoney() + money);
	}

	private static void release(final Connection con) {
		if (con != null) {
			try {
				con.setAutoCommit(true);
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void validation(final Member toMember) {
		if (toMember.getMemberId().equals("ex")) {
			throw new IllegalStateException("예외 발생");
		}
	}
}
