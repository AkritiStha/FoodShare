import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class ApproveNGO {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/foodshare?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        String dbpassword = "*!akriti434293*!";
        
        try (Connection conn = DriverManager.getConnection(url, user, dbpassword);
             PreparedStatement stmt = conn.prepareStatement("UPDATE users SET approved = 1 WHERE role = 'ngo'")) {
             
             int rows = stmt.executeUpdate();
             System.out.println("Approved " + rows + " NGO accounts in the database.");
             
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
