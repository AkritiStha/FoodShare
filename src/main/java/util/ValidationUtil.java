package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Server-side validation helpers.
 * All methods return null on success or an error message string on failure.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9]{7,15}$");

    private static final DateTimeFormatter DT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private ValidationUtil() {}

    /** Checks a string is non-null and non-blank. */
    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /** Validates an email address format. */
    public static String validateEmail(String email) {
        if (isBlank(email))                      return "Email is required.";
        if (!EMAIL_PATTERN.matcher(email.trim()).matches())
            return "Email format is invalid.";
        return null;
    }

    /** Validates a password: minimum 8 chars, at least one letter and one digit. */
    public static String validatePassword(String password) {
        if (isBlank(password))                   return "Password is required.";
        if (password.length() < 8)               return "Password must be at least 8 characters.";
        if (!password.matches(".*[A-Za-z].*"))   return "Password must contain at least one letter.";
        if (!password.matches(".*\\d.*"))         return "Password must contain at least one digit.";
        return null;
    }

    /** Validates a name field: non-blank, 2–100 characters. */
    public static String validateName(String name) {
        if (isBlank(name))                       return "Name is required.";
        if (name.trim().length() < 2)            return "Name must be at least 2 characters.";
        if (name.trim().length() > 100)          return "Name must not exceed 100 characters.";
        return null;
    }

    /** Validates a phone number format (must be 7-15 digits, optional leading +). */
    public static String validatePhone(String phone) {
        if (isBlank(phone))                      return "Phone number is required.";
        if (!PHONE_PATTERN.matcher(phone.trim()).matches())
            return "Phone number must be between 7 and 15 digits (e.g., +44123456789).";
        return null;
    }

    /** Validates address: non-blank, 5-255 characters. */
    public static String validateAddress(String address) {
        if (isBlank(address))                    return "Address is required.";
        if (address.trim().length() < 5)         return "Address must be at least 5 characters.";
        if (address.trim().length() > 255)       return "Address must not exceed 255 characters.";
        return null;
    }

    /** Parses a datetime-local HTML input string (yyyy-MM-ddTHH:mm). */
    public static LocalDateTime parseDateTime(String value) {
        if (isBlank(value)) return null;
        try {
            return LocalDateTime.parse(value.trim(), DT_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** Validates that an expiry date is in the future. */
    public static String validateExpiryDate(String expiryStr) {
        if (isBlank(expiryStr))                  return "Expiry date is required.";
        LocalDateTime expiry = parseDateTime(expiryStr);
        if (expiry == null)                      return "Expiry date format is invalid.";
        if (!expiry.isAfter(LocalDateTime.now())) return "Expiry date must be in the future.";
        return null;
    }

    /** Validates quantity: positive decimal number. */
    public static String validateQuantity(String quantityStr) {
        if (isBlank(quantityStr))                return "Quantity is required.";
        try {
            double qty = Double.parseDouble(quantityStr.trim());
            if (qty <= 0)                        return "Quantity must be greater than zero.";
        } catch (NumberFormatException e) {
            return "Quantity must be a valid number.";
        }
        return null;
    }

    /** Sanitises a string for safe display (escapes HTML special chars). */
    public static String sanitise(String input) {
        if (input == null) return "";
        return input.replace("&",  "&amp;")
                .replace("<",  "&lt;")
                .replace(">",  "&gt;")
                .replace("\"", "&quot;")
                .replace("'",  "&#x27;");
    }
}
