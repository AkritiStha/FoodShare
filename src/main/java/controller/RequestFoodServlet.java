package controller;

import model.User;
import service.RequestService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * POST-only: NGO submits a request for an available food item.
 * Redirects back to the search page with success/error feedback.
 */
@WebServlet("/ngo/requestFood")
public class RequestFoodServlet extends HttpServlet {

    private final RequestService requestService = new RequestService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        User user = (User) req.getSession().getAttribute("user");

        int foodItemId;
        try {
            foodItemId = Integer.parseInt(req.getParameter("foodItemId"));
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/ngo/searchFood");
            return;
        }

        String message = req.getParameter("message");
        String error   = requestService.submitRequest(foodItemId, user.getId(), message);

        String redirectBase = req.getContextPath() + "/ngo/searchFood?";
        if (error != null) {
            resp.sendRedirect(redirectBase + "error=" + URLEncoder.encode(error, "UTF-8"));
        } else {
            resp.sendRedirect(redirectBase + "success=" +
                    URLEncoder.encode("Request submitted successfully! Awaiting donor approval.", "UTF-8"));
        }
    }
}
