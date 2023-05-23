package hello.jdbc.repository;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberRepositoryV5 implements MemberRepository {

	private final JdbcTemplate template;

	public MemberRepositoryV5(final DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	public Member save(Member member) {
		String sql = "insert into member(member_id, money) values (?, ?)";
		template.update(sql, member.getMemberId(), member.getMoney());
		return member;
	}

	@Override
	public Member findById(String memberId) {
		String sql = "select * from member where member_id = ?";
		return template.queryForObject(sql, memberRowMapper(), memberId);
	}

	@Override
	public void update(String memberId, int money) {
		String sql = "update member set money = ? where member_id = ? ";
		template.update(sql, money, memberId);
	}

	@Override
	public void delete(String memberId) {
		String sql = "delete from member where member_id = ? ";
		template.update(sql, memberId);
	}

	private RowMapper<Member> memberRowMapper() {
		return (rs, rowNum) -> new Member(
			rs.getString("member_id"),
			rs.getInt("money")
		);
	}

}
