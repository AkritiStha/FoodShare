package controller;

import service.FoodService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/** Displays a donor's food listings. */
@WebServlet("/donor/myListings")
public class MyListingsServlet extends HttpServlet {

    private final FoodService foodService = new FoodService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("user");
        try {
            // Auto-mark expired items before showing
            foodService.markExpired();
            req.setAttribute("listings", foodService.getDonorListings(user.getId()));
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Failed to load listings.");
        }

        // Pass success/error from redirect params
        if (req.getParameter("success") != null)
            req.setAttribute("success", req.getParameter("success"));
        if (req.getParameter("error") != null)
            req.setAttribute("error", req.getParameter("error"));

        req.getRequestDispatcher("/donor/myListings.jsp").forward(req, resp);
    }
}
