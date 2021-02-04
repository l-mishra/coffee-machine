package coffee.machine.exceptions;

public class InvalidBeverageException extends RuntimeException {

    public InvalidBeverageException(String msg) {
        super(msg);
    }
}
