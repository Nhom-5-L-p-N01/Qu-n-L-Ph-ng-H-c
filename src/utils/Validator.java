package utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Pattern;

/**
 * Cac ham kiem tra du lieu dau vao dung chung cho toan he thong.
 * Tach rieng ra package utils de service/repository khong phai lap lai
 * logic validate, dung dung tinh chat "ham ho tro dung chung" ma de bai yeu cau.
 */
public final class Validator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{9,11}$");

    private Validator() {
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return isNotEmpty(phone) && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isPositiveInteger(int value) {
        return value > 0;
    }

    /** Kiem tra thoi gian ket thuc phai lon hon thoi gian bat dau (rule bat buoc trong de bai). */
    public static boolean isValidTimeRange(LocalTime batDau, LocalTime ketThuc) {
        return batDau != null && ketThuc != null && batDau.isBefore(ketThuc);
    }

    /** Khong cho phep dat phong cho ngay trong qua khu. */
    public static boolean isNotPastDate(LocalDate ngay) {
        return ngay != null && !ngay.isBefore(LocalDate.now());
    }
}
