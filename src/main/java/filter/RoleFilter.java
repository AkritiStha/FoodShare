package filter;

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
public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String uri  = req.getRequestURI();
        String ctx  = req.getContextPath();
        String path = uri.substring(ctx.length());
        String role = user.getRole();

        // Stricter role-based checks
        boolean forbidden = false;
        if (path.startsWith("/donor/") && !"donor".equals(role) && !"admin".equals(role)) {
            forbidden = true;
        } else if (path.startsWith("/ngo/") && !"ngo".equals(role) && !"admin".equals(role)) {
            forbidden = true;
        } else if (path.startsWith("/admin/") && !"admin".equals(role)) {
            forbidden = true;
        }

        if (forbidden) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "You do not have permission to access this area.");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override public void init(FilterConfig cfg) {}
    @Override public void destroy() {}
}