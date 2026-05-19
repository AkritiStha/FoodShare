import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBTester {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/foodshare?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "*!akriti434293*!";
        
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Connection successful!");
            ResultSet rs = stmt.executeQuery("SELECT email, role, approved FROM users WHERE email='admin@foodshare.com'");
            
            boolean found = false;
            while(rs.next()) {
                found = true;
                System.out.println("User found: " + rs.getString("email") + " | Role: " + rs.getString("role") + " | Approved: " + rs.getInt("approved"));
            }
            if (!found) {
                System.out.println("Admin user NOT FOUND in database!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
