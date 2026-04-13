package util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DBConnection – provides JDBC connections to the FoodShare MySQL database.
 * Reads db.properties from the classpath so credentials are not hard-coded.
 *
 * Usage:
 *   try (Connection conn = DBConnection.getConnection()) { ... }
 */
public class DBConnection {

    private static final String PROPERTIES_FILE = "/db.properties";

    private static String url;
    private static String username;
    private static String password;

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

    /** Returns a new JDBC connection from DriverManager. */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    private DBConnection() { /* utility class – no instances */ }
}
