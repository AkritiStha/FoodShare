package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Handles the contact form GET (show) and GET-with-params (simulate submission).
 * In a real system this would send an email; here it redirects with a success flag.
 */
@WebServlet("/contact")
public class ContactServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // If form params present, treat as a submission
        if (req.getParameter("cname") != null && !req.getParameter("cname").isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/contact?sent=1");
            return;
        }
        req.getRequestDispatcher("/common/contact.jsp").forward(req, resp);
    }
}
