package controller;

import service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * Handles GET (display registration form) and POST (create account).
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/common/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String name     = req.getParameter("name");
        String email    = req.getParameter("email");
        String password = req.getParameter("password");
        String confirm  = req.getParameter("confirmPassword");
        String role     = req.getParameter("role");
        String phone    = req.getParameter("phone");
        String address  = req.getParameter("address");

        // Simple confirm-password check before handing to service
        if (password == null || !password.equals(confirm)) {
            req.setAttribute("error", "Passwords do not match.");
            req.setAttribute("formData", req.getParameterMap());
            req.getRequestDispatcher("/common/register.jsp").forward(req, resp);
            return;
        }

        String error = userService.register(name, email, password, role, phone, address);
        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("formData", req.getParameterMap());
            req.getRequestDispatcher("/common/register.jsp").forward(req, resp);
            return;
        }

        // Distinguish success message for NGOs (pending approval) vs donors
        if ("ngo".equals(role)) {
            req.setAttribute("success",
                    "Registration submitted! Your account is pending admin approval. " +
                            "You will be able to log in once approved.");
        } else {
            req.setAttribute("success", "Registration successful! You can now log in.");
        }
        req.getRequestDispatcher("/common/login.jsp").forward(req, resp);
    }
}
