package controller;

import model.User;
import service.RequestService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/** Shows an NGO's request history. */
@WebServlet("/ngo/myRequests")
public class MyRequestsServlet extends HttpServlet {

    private final RequestService requestService = new RequestService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("user");
        try {
            req.setAttribute("requests", requestService.getNgoRequests(user.getId()));
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Failed to load your requests.");
        }

        if (req.getParameter("success") != null)
            req.setAttribute("success", req.getParameter("success"));
        if (req.getParameter("error") != null)
            req.setAttribute("error", req.getParameter("error"));

        req.getRequestDispatcher("/ngo/myRequests.jsp").forward(req, resp);
    }
}
