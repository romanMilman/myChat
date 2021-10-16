package serverSide;

public class IllegalMessageTypeException extends Exception {
	public IllegalMessageTypeException(String errorMessage) {
		super(errorMessage);
	}
}
