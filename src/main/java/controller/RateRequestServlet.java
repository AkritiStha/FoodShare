package controller;

import model.User;
import service.RequestService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;

/** POST-only: NGO submits a 1–5 star rating for a completed donation. */
@WebServlet("/ngo/rateRequest")
public class RateRequestServlet extends HttpServlet {

    private final RequestService requestService = new RequestService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        User user = (User) req.getSession().getAttribute("user");

        int requestId, rating;
        try {
            requestId = Integer.parseInt(req.getParameter("requestId"));
            rating    = Integer.parseInt(req.getParameter("rating"));
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/ngo/myRequests");
            return;
        }

        String note  = req.getParameter("ratingNote");
        String error = requestService.submitRating(requestId, user.getId(), rating, note);

        String base = req.getContextPath() + "/ngo/myRequests?";
        if (error != null) {
            resp.sendRedirect(base + "error=" + URLEncoder.encode(error, "UTF-8"));
        } else {
            resp.sendRedirect(base + "success=" + URLEncoder.encode("Thank you for your rating!", "UTF-8"));
        }
    }
}
