package hello.jdbc.propagation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final LogRepository logRepository;

	@Transactional
	public void joinV1(String username) {

		log.info("== memberRepository start ==");
		memberRepository.save(new Member(username));
		log.info("== memberRepository end ==");
		log.info("== logRepository start ==");
		logRepository.save(new Log(username));
		log.info("== logRepository end ==");
	}
	
	@Transactional
	public void joinV2(String username) {

		log.info("== memberRepository start ==");
		memberRepository.save(new Member(username));
		log.info("== memberRepository end ==");
		log.info("== logRepository start ==");

		try {
			logRepository.save(new Log(username));

		} catch (RuntimeException e) {
			log.info("log save fail logMsg = {}", username);
			log.info("정상 흐름 반환");
		}
		log.info("== logRepository end ==");
	}
}
