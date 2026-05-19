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

/** NGO Dashboard – overview of requests and notifications. */
@WebServlet("/ngo/dashboard")
public class NgoDashboardServlet extends HttpServlet {

    private final RequestService      requestService      = new RequestService();
    private final NotificationService notificationService = new NotificationService();
    private final FoodService         foodService         = new FoodService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("user");
        try {
            req.setAttribute("myRequests",      requestService.getNgoRequests(user.getId()));
            req.setAttribute("notifications",   notificationService.getNotificationsForUser(user.getId()));
            req.setAttribute("unreadCount",     notificationService.getUnreadCount(user.getId()));
            req.setAttribute("availableCount",  foodService.searchAvailable(null, 0, 0).size());
            notificationService.markAllRead(user.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Failed to load dashboard data.");
        }
        req.getRequestDispatcher("/ngo/ngoDashboard.jsp").forward(req, resp);
    }
}
