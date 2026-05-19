package controller;

import service.FoodService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Allows NGOs to search available food by keyword and location.
 * Results are sorted nearest-first using the Haversine formula.
 */
@WebServlet("/ngo/searchFood")
public class SearchFoodServlet extends HttpServlet {

    private final FoodService foodService = new FoodService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String keyword = req.getParameter("keyword");
        String latStr  = req.getParameter("lat");
        String lonStr  = req.getParameter("lon");

        double lat = parse(latStr);
        double lon = parse(lonStr);

        try {
            req.setAttribute("foodItems", foodService.searchAvailable(keyword, lat, lon));
            req.setAttribute("keyword",   keyword);
            req.setAttribute("lat",       latStr);
            req.setAttribute("lon",       lonStr);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Search failed. Please try again.");
        }

        if (req.getParameter("success") != null)
            req.setAttribute("success", req.getParameter("success"));
        if (req.getParameter("error") != null)
            req.setAttribute("error", req.getParameter("error"));

        req.getRequestDispatcher("/ngo/searchFood.jsp").forward(req, resp);
    }

    private double parse(String s) {
        if (s == null || s.trim().isEmpty()) return 0.0;
        try { return Double.parseDouble(s.trim()); }
        catch (NumberFormatException e) { return 0.0; }
    }
}
