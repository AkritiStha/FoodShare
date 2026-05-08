package service;

import dao.NotificationDAO;
import model.Notification;

import java.sql.SQLException;
import java.util.List;

/**
 * Business logic for in-app notifications.
 */
public class NotificationService {

    private final NotificationDAO notificationDAO = new NotificationDAO();

    public List<Notification> getNotificationsForUser(int userId) throws SQLException {
        return notificationDAO.findByUser(userId);
    }

    public int getUnreadCount(int userId) throws SQLException {
        return notificationDAO.countUnread(userId);
    }

    public void markAllRead(int userId) throws SQLException {
        notificationDAO.markAllRead(userId);
    }

    public void sendNotification(int userId, String message) throws SQLException {
        notificationDAO.createNotification(new Notification(userId, message));
    }
}
