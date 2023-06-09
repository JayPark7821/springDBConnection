package hello.jdbc.order;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class OrderServiceTest {

	@Autowired
	OrderService orderService;
	@Autowired
	OrderRepository orderRepository;

	@Test
	void complete() throws Exception {
		//given
		Order order = new Order();
		order.setUsername("정상");

		//when
		orderService.order(order);

		//then
		final Order findOrder = orderRepository.findById(order.getId()).get();
		assertThat(findOrder.getPayStatus()).isEqualTo("완료");
	}

	@Test
	void runtimeException() throws Exception {
		//given
		Order order = new Order();
		order.setUsername("예외");

		//when
		assertThatThrownBy(() -> orderService.order(order))
			.isInstanceOf(RuntimeException.class);

		//then
		assertThat(orderRepository.findById(order.getId())).isEmpty();

	}

	@Test
	void bizException() throws Exception {
		//given
		Order order = new Order();
		order.setUsername("잔고부족");

		//when
		try {
			orderService.order(order);
		} catch (NotEnoughMoneyException e) {
			log.info("고객에게 잔고 부족 안내");
		}
		//then
		assertThat(orderRepository.findById(order.getId()).get().getPayStatus()).isEqualTo("대기");

	}
}