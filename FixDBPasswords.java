import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class FixDBPasswords {
    public static void main(String[] args) {
        String plain = "Password1!";
        String newHash = BCrypt.hashpw(plain, BCrypt.gensalt(10));
        System.out.println("Generated new hash: " + newHash);
        
        String url = "jdbc:mysql://localhost:3306/foodshare?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        String dbpassword = "*!akriti434293*!";
        
        try (Connection conn = DriverManager.getConnection(url, user, dbpassword);
             PreparedStatement stmt = conn.prepareStatement("UPDATE users SET password = ?")) {
             
             stmt.setString(1, newHash);
             int rows = stmt.executeUpdate();
             System.out.println("Updated " + rows + " users with the new password hash for 'Password1!'");
             
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
