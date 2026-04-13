package filter;

import model.User;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * RoleFilter – enforces role-based access control for the three protected
 * area prefixes: /donor/*, /ngo/*, /admin/*.
 *
 * Must run AFTER AuthenticationFilter (guaranteed by declaration order in web.xml).
 */
@WebFilter({"/donor/*", "/ngo/*", "/admin/*"})
public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            // Should have been caught by AuthenticationFilter, but guard anyway
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String uri  = req.getRequestURI();
        String role = user.getRole();

        boolean allowed =
                (uri.contains("/donor/") && "donor".equals(role)) ||
                        (uri.contains("/ngo/")   && "ngo".equals(role))   ||
                        (uri.contains("/admin/") && "admin".equals(role));

        if (!allowed) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "You do not have permission to access this page.");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override public void init(FilterConfig cfg) {}
    @Override public void destroy() {}
}
