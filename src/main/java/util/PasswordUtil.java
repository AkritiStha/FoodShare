package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class providing BCrypt-based password hashing and verification
 * for the FoodShare authentication system.
 *
 * <p><strong>Role in MVC Architecture:</strong><br>
 * This class belongs to the utility ({@code util}) layer and is used
 * exclusively by {@link service.UserService} during user registration
 * (to hash a new password before persistence) and login (to verify a
 * submitted password against the stored hash). No DAO or controller
 * should interact with raw passwords directly.
 *
 * <p><strong>Security design:</strong><br>
 * Passwords are hashed using the BCrypt adaptive hashing algorithm via the
 * {@code jbcrypt-0.4} library ({@code org.mindrot.jbcrypt}). A unique,
 * randomly generated salt is embedded in every hash, so two users with
 * the same password will produce different hash strings. The work factor
 * (salt rounds) is fixed at {@value #SALT_ROUNDS}, which provides a
 * suitable balance between brute-force resistance and response time on
 * typical server hardware.
 *
 * <p>Plain-text passwords are never stored, logged, or returned by any
 * method in this class.
 *
 * <p>This class is not instantiable; all members are static.
 *
 * @author  FoodShare Team
 * @version 1.0
 * @see     org.mindrot.jbcrypt.BCrypt
 * @see     service.UserService
 */
public class PasswordUtil {

    /**
     * BCrypt work factor (number of hashing rounds).
     * A value of {@code 10} means 2^10 = 1024 iterations.
     * Higher values increase security but also increase hashing time.
     */

    private static final int SALT_ROUNDS = 10;

    /**
     * Private constructor — prevents instantiation of this utility class.
     */

    private PasswordUtil() { /* utility class */ }

    /**
     * Hashes a plain-text password using the BCrypt algorithm and a
     * newly generated random salt.
     *
     * <p>This method should be called during user registration before the
     * password is passed to {@link dao.UserDAO#createUser(model.User)}.
     * The resulting 60-character hash string is safe to store in the
     * {@code users.password} database column.
     *
     * <p><strong>Behaviour:</strong> A fresh BCrypt salt is generated on
     * every invocation using {@link BCrypt#gensalt(int)} with
     * {@value #SALT_ROUNDS} rounds. The salt is embedded in the returned
     * hash, so it does not need to be stored separately.
     *
     * @param plainPassword the raw password string supplied by the user;
     *                      must not be {@code null} or empty
     * @return a BCrypt hash string of exactly 60 characters that encodes
     *         both the salt and the hashed password
     * @throws IllegalArgumentException if {@code plainPassword} is {@code null}
     *                                  or empty
     */

    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password must not be null or empty.");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(SALT_ROUNDS));
    }

    /**
     * Verifies whether a plain-text password matches a previously generated
     * BCrypt hash.
     *
     * <p>This method should be called during login, after the candidate
     * password has been retrieved from the HTTP request and the stored hash
     * has been retrieved from the database by {@link dao.UserDAO#findByEmail(String)}.
     *
     * <p><strong>Behaviour:</strong> Internally delegates to
     * {@link BCrypt#checkpw(String, String)}, which extracts the salt from
     * the stored hash, re-hashes the plain password with that same salt, and
     * compares the results. An {@link IllegalArgumentException} thrown by the
     * BCrypt library (caused by a malformed hash string) is caught and treated
     * as a non-matching result, returning {@code false} instead of propagating
     * the exception.
     *
     * @param plainPassword  the raw password string submitted by the user at
     *                       login; must not be {@code null}
     * @param hashedPassword the BCrypt hash string loaded from the database;
     *                       must not be {@code null}
     * @return {@code true} if {@code plainPassword} produces the same hash as
     *         {@code hashedPassword}; {@code false} if the passwords do not
     *         match, or if either argument is {@code null}, or if the hash
     *         string is malformed
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
