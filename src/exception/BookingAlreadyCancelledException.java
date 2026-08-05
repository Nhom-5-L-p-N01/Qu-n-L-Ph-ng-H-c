package exception;

public class BookingAlreadyCancelledException extends Exception {
    public BookingAlreadyCancelledException(String message) {
        super(message);
    }
}
