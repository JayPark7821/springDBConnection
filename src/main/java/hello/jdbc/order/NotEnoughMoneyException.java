package hello.jdbc.order;

public class NotEnoughMoneyException extends Exception {
	public NotEnoughMoneyException(final String message) {
		super(message);
	}
}
