import org.mindrot.jbcrypt.BCrypt;

public class TestBCrypt {
    public static void main(String[] args) {
        String plain = "Password1!";
        String hash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lFa6";
        try {
            boolean matches = BCrypt.checkpw(plain, hash);
            System.out.println("Matches: " + matches);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
