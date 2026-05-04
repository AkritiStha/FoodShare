package controller;

import model.User;
import service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Handles GET (display login form) and POST (authenticate) for /login.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // If user is already logged in, redirect to their dashboard
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            redirectToDashboard((User) session.getAttribute("user"), req, resp);
            return;
        }
        req.getRequestDispatcher("/common/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String email    = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            User user = userService.login(email, password);

            if (user == null) {
                req.setAttribute("error",
                        "Invalid email/password, or your account is pending admin approval.");
                req.getRequestDispatcher("/common/login.jsp").forward(req, resp);
                return;
            }

            // Create a fresh session (prevent session fixation)
            req.getSession(false);
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(30 * 60); // 30 minutes

            // Redirect to originally requested URL if present
            String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
            session.removeAttribute("redirectAfterLogin");

            if (redirectUrl != null && !redirectUrl.isBlank()) {
                resp.sendRedirect(req.getContextPath() + redirectUrl);
            } else {
                redirectToDashboard(user, req, resp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "A server error occurred. Please try again.");
            req.getRequestDispatcher("/common/login.jsp").forward(req, resp);
        }
    }

    private void redirectToDashboard(User user, HttpServletRequest req,
                                     HttpServletResponse resp) throws IOException {
        String ctx = req.getContextPath();
        switch (user.getRole()) {
            case "admin"  -> resp.sendRedirect(ctx + "/admin/dashboard");
            case "ngo"    -> resp.sendRedirect(ctx + "/ngo/dashboard");
            default       -> resp.sendRedirect(ctx + "/donor/dashboard");
        }
    }
}
