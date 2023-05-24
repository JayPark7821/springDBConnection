package hello.jdbc.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;

	@Transactional
	public void order(Order order) throws NotEnoughMoneyException {
		log.info("call order");
		orderRepository.save(order);

		log.info("결제 프로세스 진입");
		if (order.getUsername().equals("예외")) {
			log.info("시스템 예외");
			throw new RuntimeException("시스템 예외");
		} else if (order.getUsername().equals("잔고부족")) {
			log.info("잔고부족 예외");
			order.setPayStatus("대기");
			throw new NotEnoughMoneyException("잔고부족 예외");
		} else {
			log.info("정상 승인");
			order.setPayStatus("완료");
		}

		log.info("결제 프로세스 완료");
	}
}
