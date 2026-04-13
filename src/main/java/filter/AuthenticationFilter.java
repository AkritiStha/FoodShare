package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * AuthenticationFilter – intercepts all requests and ensures the user is
 * logged in. Public pages (login, register, about, contact, static assets)
 * are allowed through without a session check.
 *
 * Session timeout is set in web.xml to 30 minutes.
 */
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    /** Paths that do not require authentication. */
    private static final Set<String> PUBLIC_PATHS = new HashSet<>(Arrays.asList(
            "/login", "/register", "/about", "/contact",
            "/common/login.jsp", "/common/register.jsp",
            "/common/about.jsp", "/common/contact.jsp",
            "/error/403.jsp", "/error/404.jsp", "/error/500.jsp"
    ));

    /** Static resource prefixes allowed without login. */
    private static final Set<String> PUBLIC_PREFIXES = new HashSet<>(Arrays.asList(
            "/css/", "/js/", "/images/"
    ));

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String contextPath = req.getContextPath();
        String uri         = req.getRequestURI().substring(contextPath.length());

        // Allow public paths and static resources through
        if (isPublic(uri)) {
            chain.doFilter(request, response);
            return;
        }

        // Check for active session
        HttpSession session = req.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("user") != null);

        if (!loggedIn) {
            // Save the originally requested URL so we can redirect after login
            session = req.getSession(true);
            session.setAttribute("redirectAfterLogin", uri);
            resp.sendRedirect(contextPath + "/login");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublic(String uri) {
        if (PUBLIC_PATHS.contains(uri)) return true;
        for (String prefix : PUBLIC_PREFIXES) {
            if (uri.startsWith(prefix)) return true;
        }
        // Allow root path and index
        return uri.equals("/") || uri.isEmpty();
    }

    @Override public void init(FilterConfig cfg) {}
    @Override public void destroy() {}
}
