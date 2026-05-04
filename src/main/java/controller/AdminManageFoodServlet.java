package controller;

import service.FoodService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;

/**
 * Admin: view and delete any food listing.
 */
@WebServlet("/admin/manageFood")
public class AdminManageFoodServlet extends HttpServlet {

    private final FoodService foodService = new FoodService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            foodService.markExpired();
            req.setAttribute("allFood", foodService.getAllFoodItems());
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Failed to load food listings.");
        }

        if (req.getParameter("success") != null)
            req.setAttribute("success", req.getParameter("success"));
        if (req.getParameter("error") != null)
            req.setAttribute("error",   req.getParameter("error"));

        req.getRequestDispatcher("/admin/manageFood.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String base = req.getContextPath() + "/admin/manageFood?";
        try {
            int foodId = Integer.parseInt(req.getParameter("foodId"));
            foodService.adminDeleteFood(foodId);
            resp.sendRedirect(base + "success=" +
                    URLEncoder.encode("Food item deleted.", "UTF-8"));
        } catch (NumberFormatException e) {
            resp.sendRedirect(base + "error=Invalid+food+ID.");
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendRedirect(base + "error=" +
                    URLEncoder.encode("Delete failed: " + e.getMessage(), "UTF-8"));
        }
    }
}
