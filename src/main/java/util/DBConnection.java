package util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class responsible for providing JDBC {@link Connection} instances
 * to the FoodShare MySQL database.
 *
 * <p><strong>Role in MVC Architecture:</strong><br>
 * This class sits at the infrastructure layer, below the DAO tier. Every DAO
 * class calls {@link #getConnection()} to obtain a live database connection.
 * It acts as the single point of configuration for all database access in the
 * application, ensuring that connection credentials are never hard-coded in
 * business or data-access code.
 *
 * <p><strong>Configuration:</strong><br>
 * Connection parameters are loaded at class initialisation from the
 * {@code db.properties} file located on the application classpath
 * (typically deployed to {@code WEB-INF/classes/db.properties}).
 * Expected keys are:
 * <ul>
 *   <li>{@code db.url}      – full JDBC connection URL</li>
 *   <li>{@code db.username} – database login username</li>
 *   <li>{@code db.password} – database login password</li>
 *   <li>{@code db.driver}   – fully-qualified JDBC driver class name
 *                             (defaults to {@code com.mysql.cj.jdbc.Driver})</li>
 * </ul>
 *
 * <p><strong>Usage pattern (try-with-resources):</strong>
 * <pre>{@code
 *   try (Connection conn = DBConnection.getConnection()) {
 *       // execute queries
 *   }
 * }</pre>
 *
 * <p>This class is not instantiable; all members are static.
 *
 * @author  FoodShare Team
 * @version 1.0
 * @see     java.sql.DriverManager
 * @see     java.sql.Connection
 */

public class DBConnection {
    /** Classpath location of the JDBC configuration properties file. */
    private static final String PROPERTIES_FILE = "/db.properties";

    /** JDBC connection URL read from {@code db.properties}. */
    private static String url;

    /** Database username read from {@code db.properties}. */
    private static String username;

    /** Database password read from {@code db.properties}. */
    private static String password;

    /**
     * Static initialiser block that loads JDBC configuration from
     * {@code db.properties} and registers the MySQL JDBC driver.
     *
     * <p>This block executes exactly once when the class is first loaded by
     * the JVM. If the properties file is missing, or the driver class cannot
     * be found, a {@link RuntimeException} is thrown immediately, preventing
     * the application from starting in a misconfigured state.
     *
     * @throws RuntimeException if {@code db.properties} is absent from the
     *                          classpath or if the JDBC driver class is not
     *                          available on the classpath
     */

    static {
        try (InputStream in = DBConnection.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (in == null) {
                throw new RuntimeException("db.properties not found on classpath.");
            }
            Properties props = new Properties();
            props.load(in);

            url      = props.getProperty("db.url");
            username = props.getProperty("db.username");
            password = props.getProperty("db.password");

            // Register MySQL JDBC driver
            Class.forName(props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to initialise DBConnection: " + e.getMessage(), e);
        }
    }

    /**
     * Opens and returns a new JDBC {@link Connection} to the FoodShare database.
     *
     * <p>Each call to this method creates a fresh connection via
     * {@link DriverManager#getConnection(String, String, String)}. Callers are
     * responsible for closing the connection after use; the recommended pattern
     * is a try-with-resources block so the connection is closed automatically
     * even if an exception occurs.
     *
     * <p><strong>Database interaction:</strong> Delegates directly to
     * {@link DriverManager}, which uses the URL, username, and password loaded
     * during class initialisation.
     *
     * @return a new, open {@link Connection} to the configured MySQL database
     * @throws SQLException if a database access error occurs, or if the URL,
     *                      username, or password loaded from {@code db.properties}
     *                      is invalid
     */

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Private constructor — prevents instantiation of this utility class.
     * All access is through the static {@link #getConnection()} method.
     */

    private DBConnection() { /* utility class – no instances */ }
}
