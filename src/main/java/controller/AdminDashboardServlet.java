package controller;

import service.FoodService;
import service.RequestService;
import service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Admin dashboard – shows platform-wide statistics at a glance.
 */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final UserService    userService    = new UserService();
    private final FoodService    foodService    = new FoodService();
    private final RequestService requestService = new RequestService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            // Auto-expire stale food items on every admin view
            foodService.markExpired();

            req.setAttribute("totalDonors",    userService.countByRole("donor"));
            req.setAttribute("totalNgos",      userService.countByRole("ngo"));
            req.setAttribute("totalFood",      foodService.countAll());
            req.setAttribute("totalRequests",  requestService.countAll());
            req.setAttribute("completedCount", requestService.countByStatus("COMPLETED"));
            req.setAttribute("pendingCount",   requestService.countByStatus("PENDING"));
            req.setAttribute("foodSaved",      foodService.totalFoodSaved());
            req.setAttribute("pendingNgos",    userService.getPendingNgos());
            req.setAttribute("topDonor",       requestService.topDonorName());
            req.setAttribute("mostRequested",  requestService.mostRequestedFood());

        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Failed to load dashboard statistics.");
        }

        req.getRequestDispatcher("/admin/adminDashboard.jsp").forward(req, resp);
    }
}
