package controller;

import model.User;
import service.RequestService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Handles GET (view incoming requests) and POST (accept/reject request).
 */
@WebServlet("/donor/requests")
public class RequestsReceivedServlet extends HttpServlet {

    private final RequestService requestService = new RequestService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("user");
        try {
            req.setAttribute("requests", requestService.getDonorRequests(user.getId()));
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Failed to load requests.");
        }

        if (req.getParameter("success") != null)
            req.setAttribute("success", req.getParameter("success"));
        if (req.getParameter("error") != null)
            req.setAttribute("error", req.getParameter("error"));

        req.getRequestDispatcher("/donor/requestsReceived.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        User user   = (User) req.getSession().getAttribute("user");
        String action = req.getParameter("action");

        int requestId;
        try {
            requestId = Integer.parseInt(req.getParameter("requestId"));
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/donor/requests");
            return;
        }

        String error = null;
        String successMsg = null;

        switch (action == null ? "" : action) {
            case "accept" -> {
                error = requestService.acceptRequest(
                        requestId, user.getId(),
                        req.getParameter("pickupTime"),
                        req.getParameter("scheduleNotes")
                );
                if (error == null) successMsg = "Request accepted and pickup scheduled.";
            }
            case "reject" -> {
                error = requestService.rejectRequest(requestId, user.getId());
                if (error == null) successMsg = "Request rejected.";
            }
            case "complete" -> {
                error = requestService.completeRequest(requestId, user.getId());
                if (error == null) successMsg = "Request marked as completed.";
            }
            default -> error = "Unknown action.";
        }

        String redirect = req.getContextPath() + "/donor/requests?";
        if (error != null) {
            redirect += "error=" + java.net.URLEncoder.encode(error, "UTF-8");
        } else {
            redirect += "success=" + java.net.URLEncoder.encode(successMsg, "UTF-8");
        }
        resp.sendRedirect(redirect);
    }
}
