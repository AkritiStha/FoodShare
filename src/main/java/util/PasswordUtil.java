package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for hashing and verifying passwords with BCrypt.
 *
 * Dependency: jbcrypt-0.4.jar (org.mindrot:jbcrypt)
 * Salt rounds: 10 (good balance of security and performance)
 */
public class PasswordUtil {

    private static final int SALT_ROUNDS = 10;

    private PasswordUtil() { /* utility class */ }

    /**
     * Hashes a plain-text password using BCrypt.
     *
     * @param plainPassword the raw password supplied by the user
     * @return a BCrypt hash string (60 characters)
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password must not be null or empty.");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(SALT_ROUNDS));
    }

    /**
     * Verifies a plain-text password against a stored BCrypt hash.
     *
     * @param plainPassword  the raw password to check
     * @param hashedPassword the stored BCrypt hash
     * @return true if the password matches; false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) return false;
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Invalid hash format – treat as mismatch
            return false;
        }
    }
}
