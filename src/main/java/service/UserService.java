package service;

import dao.UserDAO;
import util.PasswordUtil;
import util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * Business logic for user-related operations.
 * Validates inputs, hashes passwords, and delegates persistence to UserDAO.
 */
public class UserService {

    private final UserDAO userDAO = new UserDAO();
    private final NotificationService notificationService = new NotificationService();

    /**
     * Registers a new user after validating inputs.
     *
     * @return null on success, or an error message string
     */
    public String register(String name, String email, String password,
                           String role, String phone, String address) {
        // Validate
        String err;
        if ((err = ValidationUtil.validateName(name))     != null) return err;
        if ((err = ValidationUtil.validateEmail(email))   != null) return err;
        if ((err = ValidationUtil.validatePassword(password)) != null) return err;
        if ((err = ValidationUtil.validatePhone(phone))    != null) return err;
        if ((err = ValidationUtil.validateAddress(address)) != null) return err;
        if (!List.of("donor", "ngo").contains(role)) return "Invalid role selected.";

        String normalizedEmail = email.trim().toLowerCase();
        String upperName = capitalizeWords(name.trim());

        try {
            if (userDAO.emailExists(normalizedEmail)) {
                return "An account with this email already exists.";
            }

            User user = new User();
            user.setName(upperName);
            user.setEmail(normalizedEmail);
            user.setPassword(PasswordUtil.hashPassword(password));
            user.setRole(role);
            user.setPhone(phone == null ? "" : phone.trim());
            user.setAddress(address == null ? "" : address.trim());

            int newId = userDAO.createUser(user);
            return newId > 0 ? null : "Registration failed. Please try again.";

        } catch (SQLException e) {
            e.printStackTrace();
            // Check if it's a duplicate key violation (SQLState 23000)
            if ("23000".equals(e.getSQLState()) || e.getErrorCode() == 1062) {
                return "An account with this email already exists.";
            }
            return "A database error occurred. Please try again.";
        }
    }

    /**
     * Authenticates a user by email and password.
     *
     * @return authenticated User, or null if credentials are wrong/account pending
     */
    public User login(String email, String password) throws SQLException {
        if (ValidationUtil.isBlank(email) || ValidationUtil.isBlank(password)) return null;

        User user = userDAO.findByEmail(email.trim().toLowerCase());
        if (user == null) return null;
        if (!PasswordUtil.verifyPassword(password, user.getPassword())) return null;

        // NGOs must be approved by admin before logging in
        if ("ngo".equals(user.getRole()) && !user.isApproved()) return null;

        return user;
    }

    public User findById(int id) throws SQLException {
        return userDAO.findById(id);
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.findAll();
    }

    public List<User> getUsersByRole(String role) throws SQLException {
        return userDAO.findByRole(role);
    }

    public List<User> getPendingNgos() throws SQLException {
        return userDAO.findPendingNgos();
    }

    public void approveUser(int userId) throws SQLException {
        userDAO.setApproval(userId, true);
        notificationService.sendNotification(userId, "Your account has been approved by the admin. You can now log in.");
    }

    public void rejectUser(int userId) throws SQLException {
        userDAO.setApproval(userId, false);
        notificationService.sendNotification(userId, "Your account request has been rejected by the admin.");
    }

    public void deleteUser(int userId) throws SQLException {
        userDAO.deleteUser(userId);
    }

    public String updateProfile(int userId, String name, String phone, String address) {
        String err;
        if ((err = ValidationUtil.validateName(name))     != null) return err;
        if ((err = ValidationUtil.validatePhone(phone))    != null) return err;
        if ((err = ValidationUtil.validateAddress(address)) != null) return err;

        try {
            User user = new User();
            user.setId(userId);
            user.setName(capitalizeWords(name.trim()));
            user.setPhone(phone == null ? "" : phone.trim());
            user.setAddress(address == null ? "" : address.trim());
            userDAO.updateProfile(user);
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to update profile.";
        }
    }

    public String changePassword(int userId, String currentPassword,
                                 String newPassword, String confirmPassword) {
        try {
            User user = userDAO.findById(userId);
            if (user == null) return "User not found.";
            if (!PasswordUtil.verifyPassword(currentPassword, user.getPassword()))
                return "Current password is incorrect.";
            if (!newPassword.equals(confirmPassword))
                return "New passwords do not match.";
            String err = ValidationUtil.validatePassword(newPassword);
            if (err != null) return err;
            userDAO.updatePassword(userId, PasswordUtil.hashPassword(newPassword));
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to change password.";
        }
    }

    public int countByRole(String role) throws SQLException {
        return userDAO.countByRole(role);
    }

    private String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) return str;
        String[] words = str.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                sb.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    sb.append(word.substring(1).toLowerCase());
                }
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }
}
