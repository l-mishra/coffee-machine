package coffee.machine.exceptions;

public class InvalidItemException extends RuntimeException {
    public InvalidItemException(String msg) {
        super(msg);
    }
}