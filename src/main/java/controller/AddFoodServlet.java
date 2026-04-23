package controller;

import model.User;
import service.FoodService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * Handles GET (show add-food form) and POST (create food listing).
 */
@WebServlet("/donor/addFood")
public class AddFoodServlet extends HttpServlet {

    private final FoodService foodService = new FoodService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/donor/addFood.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        User user = (User) req.getSession().getAttribute("user");

        String error = foodService.addFoodItem(
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
            req.setAttribute("error", error);
            req.getRequestDispatcher("/donor/addFood.jsp").forward(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/donor/myListings?success=Food+item+added+successfully.");
    }
}
