package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Utility class providing server-side input validation helpers for the
 * FoodShare application.
 *
 * <p><strong>Role in MVC Architecture:</strong><br>
 * This class belongs to the utility ({@code util}) layer and is called
 * from service classes (e.g. {@link service.UserService},
 * {@link service.FoodService}) to validate and sanitise data received from
 * HTTP request parameters before it is passed to DAO classes for
 * persistence. It serves as the single, consistent source of validation
 * logic, preventing duplicate validation rules from being scattered across
 * multiple servlets or services.
 *
 * <p><strong>Design:</strong><br>
 * Each validation method returns {@code null} on success (no error) or a
 * human-readable error message string on failure. This convention allows
 * service methods to check the result with a simple {@code null} comparison
 * and forward the error message directly to the JSP view when needed.
 *
 * <p>HTML-special-character sanitisation is provided by {@link #sanitise(String)}
 * to mitigate Cross-Site Scripting (XSS) risks before any user-supplied
 * content is rendered in JSP output.
 *
 * <p>This class is not instantiable; all members are static.
 *
 * @author  FoodShare Team
 * @version 1.0
 * @see     service.UserService
 * @see     service.FoodService
 */

public class ValidationUtil {

    /**
     * Regular expression pattern for validating email addresses.
     * Accepts the format {@code local@domain.tld} where the TLD is at least
     * two characters long. Allows letters, digits, and the characters
     * {@code +}, {@code _}, {@code .}, and {@code -} in the local part.
     */

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * Regular expression pattern for validating phone numbers.
     * Permits digits, spaces, and the characters {@code +}, {@code -},
     * {@code (}, and {@code )} in a range of 7 to 20 characters.
     */

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9]{7,15}$");

    /**
     * Date-time formatter matching the value format produced by an HTML
     * {@code <input type="datetime-local">} element: {@code yyyy-MM-dd'T'HH:mm}.
     */

    private static final DateTimeFormatter DT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    /**
     * Private constructor — prevents instantiation of this utility class.
     */

    private ValidationUtil() {}

    /**
     * Tests whether a string value is {@code null} or contains only
     * whitespace characters.
     *
     * <p>Used throughout the service layer as a prerequisite check before
     * any further validation is performed on a field value.
     *
     * @param value the string to test; may be {@code null}
     * @return {@code true} if {@code value} is {@code null} or blank;
     *         {@code false} otherwise
     */

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Validates the format of an email address submitted during registration
     * or profile update.
     *
     * <p><strong>Validation rules applied:</strong>
     * <ol>
     *   <li>The value must not be blank.</li>
     *   <li>The trimmed value must match {@link #EMAIL_PATTERN}.</li>
     * </ol>
     *
     * @param email the raw email string from the HTTP request parameter
     * @return {@code null} if the email is valid; a descriptive error message
     *         string if validation fails
     */

    public static String validateEmail(String email) {
        if (isBlank(email))                      return "Email is required.";
        if (!EMAIL_PATTERN.matcher(email.trim()).matches())
            return "Email format is invalid.";
        return null;
    }

    /**
     * Validates a password submitted during registration or a password-change
     * request.
     *
     * <p><strong>Validation rules applied:</strong>
     * <ol>
     *   <li>The value must not be blank.</li>
     *   <li>The password must be at least 8 characters long.</li>
     *   <li>The password must contain at least one letter ({@code [A-Za-z]}).</li>
     *   <li>The password must contain at least one digit ({@code [0-9]}).</li>
     * </ol>
     *
     * <p>This method validates the raw plain-text password before it is
     * passed to {@link PasswordUtil#hashPassword(String)} for hashing.
     *
     * @param password the plain-text password string from the HTTP request
     * @return {@code null} if the password meets all requirements; a
     *         descriptive error message string if any rule is violated
     */

    public static String validatePassword(String password) {
        if (isBlank(password))                   return "Password is required.";
        if (password.length() < 8)               return "Password must be at least 8 characters.";
        if (!password.matches(".*[A-Za-z].*"))   return "Password must contain at least one letter.";
        if (!password.matches(".*\\d.*"))         return "Password must contain at least one digit.";
        return null;
    }

    /**
     * Validates a user or organisation name field.
     *
     * <p><strong>Validation rules applied:</strong>
     * <ol>
     *   <li>The value must not be blank.</li>
     *   <li>After trimming, the name must be at least 2 characters.</li>
     *   <li>After trimming, the name must not exceed 100 characters.</li>
     * </ol>
     *
     * @param name the name string from the HTTP request parameter
     * @return {@code null} if the name is valid; a descriptive error message
     *         string if validation fails
     */

    public static String validateName(String name) {
        if (isBlank(name))                       return "Name is required.";
        if (name.trim().length() < 2)            return "Name must be at least 2 characters.";
        if (name.trim().length() > 100)          return "Name must not exceed 100 characters.";
        return null;
    }

    /**
     * Validates an optional phone number field.
     *
     * <p>Because the phone field is optional, a blank or {@code null} value
     * is considered valid and returns {@code null} immediately. A non-blank
     * value is checked against {@link #PHONE_PATTERN}.
     *
     * @param phone the phone number string from the HTTP request parameter;
     *              may be {@code null} or empty for optional fields
     * @return {@code null} if the phone is blank (optional) or matches the
     *         expected pattern; a descriptive error message if the format
     *         is invalid
     */

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

    /**
     * Parses a datetime string produced by an HTML {@code datetime-local}
     * input element into a {@link LocalDateTime} object.
     *
     * <p>The expected input format is {@code yyyy-MM-dd'T'HH:mm}, for example
     * {@code 2025-12-31T23:59}. Returns {@code null} for blank input or
     * unparseable strings rather than throwing an exception, allowing callers
     * to detect and report parse failures gracefully.
     *
     * @param value the datetime string from the HTML form field; may be
     *              {@code null} or empty
     * @return the parsed {@link LocalDateTime}, or {@code null} if the value
     *         is blank or does not match the expected format
     */

    public static LocalDateTime parseDateTime(String value) {
        if (isBlank(value)) return null;
        try {
            return LocalDateTime.parse(value.trim(), DT_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Validates an expiry date-time string submitted when creating or
     * editing a food listing.
     *
     * <p><strong>Validation rules applied:</strong>
     * <ol>
     *   <li>The value must not be blank.</li>
     *   <li>The value must be parseable by {@link #parseDateTime(String)}.</li>
     *   <li>The parsed date-time must be strictly in the future relative to
     *       the server's current system time ({@link LocalDateTime#now()}).</li>
     * </ol>
     *
     * @param expiryStr the raw datetime-local string from the HTTP request
     * @return {@code null} if the expiry date is valid and in the future;
     *         a descriptive error message string if validation fails
     */

    public static String validateExpiryDate(String expiryStr) {
        if (isBlank(expiryStr))                  return "Expiry date is required.";
        LocalDateTime expiry = parseDateTime(expiryStr);
        if (expiry == null)                      return "Expiry date format is invalid.";
        if (!expiry.isAfter(LocalDateTime.now())) return "Expiry date must be in the future.";
        return null;
    }

    /**
     * Validates a food quantity value submitted from the add-food or
     * edit-food form.
     *
     * <p><strong>Validation rules applied:</strong>
     * <ol>
     *   <li>The value must not be blank.</li>
     *   <li>The value must be parseable as a {@code double}.</li>
     *   <li>The parsed numeric value must be strictly greater than zero.</li>
     * </ol>
     *
     * @param quantityStr the raw quantity string from the HTTP request parameter
     * @return {@code null} if the quantity is a valid positive number;
     *         a descriptive error message string if validation fails
     */

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

    /**
     * Sanitises a user-supplied string for safe inclusion in HTML output,
     * mitigating Cross-Site Scripting (XSS) vulnerabilities.
     *
     * <p>Replaces the five HTML-special characters with their corresponding
     * HTML entities:
     * <ul>
     *   <li>{@code &}  → {@code &amp;}</li>
     *   <li>{@code <}  → {@code &lt;}</li>
     *   <li>{@code >}  → {@code &gt;}</li>
     *   <li>{@code "}  → {@code &quot;}</li>
     *   <li>{@code '}  → {@code &#x27;}</li>
     * </ul>
     *
     * <p>Returns an empty string if the input is {@code null}.
     *
     * @param input the raw string to sanitise; may be {@code null}
     * @return the HTML-escaped version of {@code input}, or an empty string
     *         if {@code input} is {@code null}
     */

    public static String sanitise(String input) {
        if (input == null) return "";
        return input.replace("&",  "&amp;")
                .replace("<",  "&lt;")
                .replace(">",  "&gt;")
                .replace("\"", "&quot;")
                .replace("'",  "&#x27;");
    }
}
