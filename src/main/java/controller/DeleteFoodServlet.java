package controller;

import service.FoodService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * POST-only: deletes a donor's food item (donor-owned items only).
 */
@WebServlet("/donor/deleteFood")
public class DeleteFoodServlet extends HttpServlet {

    private final FoodService foodService = new FoodService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("user");

        try {
            int id = Integer.parseInt(req.getParameter("id"));
            String error = foodService.deleteFoodItem(id, user.getId());

            if (error != null) {
                resp.sendRedirect(req.getContextPath() + "/donor/myListings?error=" +
                        java.net.URLEncoder.encode(error, "UTF-8"));
            } else {
                resp.sendRedirect(req.getContextPath() + "/donor/myListings?success=Food+item+deleted.");
            }
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/donor/myListings");
        }
    }
}
