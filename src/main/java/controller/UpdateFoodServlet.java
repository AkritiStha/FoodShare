package controller;

import model.FoodItem;
import service.FoodService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Handles GET (pre-fill edit form) and POST (save updates) for a food item.
 */
@WebServlet("/donor/updateFood")
public class UpdateFoodServlet extends HttpServlet {

    private final FoodService foodService = new FoodService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("user");
        String idStr = req.getParameter("id");

        try {
            int id = Integer.parseInt(idStr);
            FoodItem item = foodService.getById(id);

            if (item == null || item.getDonorId() != user.getId()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
                return;
            }
            req.setAttribute("foodItem", item);
            req.getRequestDispatcher("/donor/editFood.jsp").forward(req, resp);

        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/donor/myListings");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        User user = (User) req.getSession().getAttribute("user");

        int foodItemId;
        try {
            foodItemId = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/donor/myListings");
            return;
        }

        String error = foodService.updateFoodItem(
                foodItemId,
                user.getId(),
                req.getParameter("name"),
                req.getParameter("quantity"),
                req.getParameter("quantityUnit"),
                req.getParameter("description"),
                req.getParameter("expiryDate"),
                req.getParameter("pickupLocation"),
                req.getParameter("latitude"),
                req.getParameter("longitude")
        );

        if (error != null) {
            try {
                FoodItem item = foodService.getById(foodItemId);
                req.setAttribute("foodItem", item);
            } catch (SQLException ex) { /* ignore */ }
            req.setAttribute("error", error);
            req.getRequestDispatcher("/donor/editFood.jsp").forward(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/donor/myListings?success=Food+item+updated.");
    }
}
