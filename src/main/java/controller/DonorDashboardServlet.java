package controller;

import model.User;
import service.FoodService;
import service.NotificationService;
import service.RequestService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Donor dashboard – shows summary stats, recent listings, and notifications.
 */
@WebServlet("/donor/dashboard")
public class DonorDashboardServlet extends HttpServlet {

    private final FoodService         foodService         = new FoodService();
    private final RequestService      requestService      = new RequestService();
    private final NotificationService notificationService = new NotificationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("user");

        try {
            req.setAttribute("myListings",
                    foodService.getDonorListings(user.getId()));

            req.setAttribute("pendingRequests",
                    requestService.getDonorRequests(user.getId()).stream()
                            .filter(r -> "PENDING".equals(r.getStatus()))
                            .toList());

            req.setAttribute("notifications",
                    notificationService.getNotificationsForUser(user.getId()));

            req.setAttribute("unreadCount",
                    notificationService.getUnreadCount(user.getId()));

            // Mark all as read after display
            notificationService.markAllRead(user.getId());

        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Failed to load dashboard data.");
        }

        req.getRequestDispatcher("/donor/donorDashboard.jsp").forward(req, resp);
    }
}
