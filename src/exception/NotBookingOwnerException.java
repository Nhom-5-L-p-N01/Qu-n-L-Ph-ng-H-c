package exception;

public class NotBookingOwnerException extends Exception {
    public NotBookingOwnerException(String message) {
        super(message);
    }
}
