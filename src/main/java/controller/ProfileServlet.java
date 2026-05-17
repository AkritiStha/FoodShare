package controller;

import model.User;
import service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/** Handles GET (show profile) and POST (update profile or change password). */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (req.getParameter("success") != null)
            req.setAttribute("success", req.getParameter("success"));
        req.getRequestDispatcher("/common/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        User user   = (User) req.getSession().getAttribute("user");
        String mode = req.getParameter("mode");

        if ("changePassword".equals(mode)) {
            String err = userService.changePassword(
                    user.getId(),
                    req.getParameter("currentPassword"),
                    req.getParameter("newPassword"),
                    req.getParameter("confirmPassword")
            );
            if (err != null) {
                req.setAttribute("error", err);
                req.getRequestDispatcher("/common/profile.jsp").forward(req, resp);
            } else {
                resp.sendRedirect(req.getContextPath() + "/profile?success=Password+changed+successfully.");
            }
        } else {
            // Update profile
            String err = userService.updateProfile(
                    user.getId(),
                    req.getParameter("name"),
                    req.getParameter("phone"),
                    req.getParameter("address")
            );
            if (err != null) {
                req.setAttribute("error", err);
                req.getRequestDispatcher("/common/profile.jsp").forward(req, resp);
            } else {
                // Refresh user in session
                try {
                    User updated = userService.findById(user.getId());
                    req.getSession().setAttribute("user", updated);
                } catch (Exception ex) { /* non-fatal */ }
                resp.sendRedirect(req.getContextPath() + "/profile?success=Profile+updated+successfully.");
            }
        }
    }
}
