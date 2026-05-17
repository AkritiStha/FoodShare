package controller;

import service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;

/**
 * Admin: view all users and approve / reject / delete accounts.
 */
@WebServlet("/admin/manageUsers")
public class AdminManageUsersServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            req.setAttribute("allUsers",    userService.getAllUsers());
            req.setAttribute("pendingNgos", userService.getPendingNgos());
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Failed to load users.");
        }

        if (req.getParameter("success") != null)
            req.setAttribute("success", req.getParameter("success"));
        if (req.getParameter("error") != null)
            req.setAttribute("error",   req.getParameter("error"));

        req.getRequestDispatcher("/admin/manageUsers.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        String base   = req.getContextPath() + "/admin/manageUsers?";

        int userId;
        try {
            userId = Integer.parseInt(req.getParameter("userId"));
        } catch (NumberFormatException e) {
            resp.sendRedirect(base + "error=Invalid+user+ID.");
            return;
        }

        try {
            switch (action == null ? "" : action) {
                case "approve" -> {
                    userService.approveUser(userId);
                    resp.sendRedirect(base + "success=" +
                            URLEncoder.encode("NGO approved successfully.", "UTF-8"));
                }
                case "reject" -> {
                    userService.rejectUser(userId);
                    resp.sendRedirect(base + "success=" +
                            URLEncoder.encode("NGO application rejected.", "UTF-8"));
                }
                case "delete" -> {
                    userService.deleteUser(userId);
                    resp.sendRedirect(base + "success=" +
                            URLEncoder.encode("User deleted.", "UTF-8"));
                }
                default -> resp.sendRedirect(base + "error=Unknown+action.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendRedirect(base + "error=" +
                    URLEncoder.encode("Database error: " + e.getMessage(), "UTF-8"));
        }
    }
}
