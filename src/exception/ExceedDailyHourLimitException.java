package exception;

public class ExceedDailyHourLimitException extends Exception {
    public ExceedDailyHourLimitException(String message) {
        super(message);
    }
}
