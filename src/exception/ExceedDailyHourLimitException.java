package exception;

public class BookingNotPendingException extends Exception {
    public BookingNotPendingException(String message) {
        super(message);
    }
}
