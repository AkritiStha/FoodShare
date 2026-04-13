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
 * Admin reports – aggregate metrics for the platform.
 */
@WebServlet("/admin/reports")
public class AdminReportsServlet extends HttpServlet {

    private final UserService    userService    = new UserService();
    private final FoodService    foodService    = new FoodService();
    private final RequestService requestService = new RequestService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            req.setAttribute("totalDonors",     userService.countByRole("donor"));
            req.setAttribute("totalNgos",        userService.countByRole("ngo"));
            req.setAttribute("totalFood",        foodService.countAll());
            req.setAttribute("totalRequests",    requestService.countAll());
            req.setAttribute("completedCount",   requestService.countByStatus("COMPLETED"));
            req.setAttribute("pendingCount",     requestService.countByStatus("PENDING"));
            req.setAttribute("rejectedCount",    requestService.countByStatus("REJECTED"));
            req.setAttribute("foodSavedKg",      foodService.totalFoodSaved());
            req.setAttribute("topDonor",         requestService.topDonorName());
            req.setAttribute("mostRequested",    requestService.mostRequestedFood());
            req.setAttribute("allRequests",      requestService.getAllRequests());
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Failed to generate report.");
        }

        req.getRequestDispatcher("/admin/reports.jsp").forward(req, resp);
    }
}
