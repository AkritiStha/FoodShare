package controller;

import model.User;
import service.RequestService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet controller that handles the donor's "Requests Received" workflow,
 * located at the URL pattern {@code /donor/requests}.
 *
 * <p><strong>Role in MVC Architecture:</strong><br>
 * This class belongs to the Controller layer of the MVC pattern. It acts as
 * the intermediary between the JSP view
 * ({@code /donor/requestsReceived.jsp}) and the
 * {@link service.RequestService} business logic class. It handles two HTTP
 * methods:
 * <ul>
 *   <li>{@code GET}  – loads and displays all requests directed at the
 *       currently logged-in donor.</li>
 *   <li>{@code POST} – processes one of three donor actions on a specific
 *       request: <em>accept</em>, <em>reject</em>, or <em>complete</em>.</li>
 * </ul>
 *
 * <p><strong>Authentication &amp; session management:</strong><br>
 * This servlet is protected by two filters declared in {@code web.xml}:
 * <ol>
 *   <li>{@link filter.AuthenticationFilter} – redirects unauthenticated
 *       requests to {@code /login} before this servlet is reached.</li>
 *   <li>{@link filter.RoleFilter} – enforces that only users with role
 *       {@code "donor"} can access the {@code /donor/*} URL space.</li>
 * </ol>
 * The logged-in {@link User} object is retrieved directly from the HTTP
 * session attribute {@code "user"} that was stored by
 * {@link LoginServlet} on successful authentication.
 *
 * <p><strong>Post-Redirect-Get pattern:</strong><br>
 * All POST actions redirect to the GET endpoint after processing, carrying
 * a {@code success} or {@code error} query parameter. This prevents form
 * re-submission on browser refresh and keeps the URL bookmarkable.
 *
 * @author  FoodShare Team
 * @version 1.0
 * @see     service.RequestService
 * @see     filter.AuthenticationFilter
 * @see     filter.RoleFilter
 */

@WebServlet("/donor/requests")
public class RequestReceivedServlet extends HttpServlet {

    /**
     * Service layer delegate that encapsulates all business logic for
     * request lifecycle management. Instantiated once per servlet instance
     * (effectively application-scoped) since {@link RequestService} is
     * stateless.
     */

    private final RequestService requestService = new RequestService();

    /**
     * Handles HTTP GET requests to {@code /donor/requests}.
     *
     * <p>Fetches all requests directed at the currently authenticated donor
     * from {@link RequestService#getDonorRequests(int)} and stores the list
     * as a request attribute before forwarding to the JSP view. Also reads
     * {@code success} and {@code error} query parameters that may have been
     * appended by a preceding POST redirect, and forwards them as request
     * attributes so the JSP can render appropriate alert banners.
     *
     * <p><strong>Session interaction:</strong> The donor's {@link User} object
     * is read from the session attribute {@code "user"} to obtain the
     * {@code donorId} used to filter requests.
     *
     * <p><strong>Database interaction:</strong> Delegates to
     * {@link service.RequestService#getDonorRequests(int)}, which calls
     * {@link dao.RequestDAO#findByDonor(int)}. A four-way JOIN populates food
     * item name, NGO name, pickup location, and scheduled pickup time in a
     * single query.
     *
     * @param req  the incoming {@link HttpServletRequest}; expected to contain
     *             the session attribute {@code "user"} and optional query
     *             parameters {@code success} and {@code error}
     * @param resp the {@link HttpServletResponse} used to forward to the view
     * @throws ServletException if the request dispatcher cannot forward to the JSP
     * @throws IOException      if an I/O error occurs during forwarding
     */

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

    /**
     * Handles HTTP POST requests to {@code /donor/requests}.
     *
     * <p>Reads the {@code action} and {@code requestId} parameters from the
     * form submission and delegates to the appropriate
     * {@link RequestService} method. Three actions are supported:
     *
     * <ul>
     *   <li><strong>{@code accept}</strong> – calls
     *       {@link RequestService#acceptRequest(int, int, String, String)}.
     *       Requires {@code pickupTime} (datetime-local string) and optionally
     *       {@code scheduleNotes}. On success, the request status transitions
     *       to {@code ACCEPTED}, a pickup schedule is created, and a
     *       notification is sent to the NGO.</li>
     *
     *   <li><strong>{@code reject}</strong> – calls
     *       {@link RequestService#rejectRequest(int, int)}.
     *       On success, the request status transitions to {@code REJECTED},
     *       the food item reverts to {@code available}, and a notification
     *       is sent to the NGO.</li>
     *
     *   <li><strong>{@code complete}</strong> – calls
     *       {@link RequestService#completeRequest(int, int)}.
     *       On success, both the request and its associated food item
     *       transition to {@code COMPLETED}, and the NGO receives a
     *       completion notification.</li>
     * </ul>
     *
     * <p><strong>Ownership enforcement:</strong> Each service method receives
     * the authenticated donor's {@code userId} and verifies internally that
     * the request belongs to that donor before applying any change, preventing
     * one donor from manipulating another donor's requests.
     *
     * <p><strong>Parameter validation:</strong> The {@code requestId} is
     * parsed as an integer; a {@link NumberFormatException} causes a silent
     * redirect back to the listing without an error message. All substantive
     * validation (status transitions, ownership) is performed in the service
     * layer.
     *
     * <p><strong>Post-Redirect-Get:</strong> After processing, the method
     * always redirects to {@code GET /donor/requests} with either a
     * {@code success} or {@code error} query parameter URL-encoded to prevent
     * injection via the query string.
     *
     * @param req  the incoming {@link HttpServletRequest}; must contain form
     *             parameters {@code action} (required) and {@code requestId}
     *             (required); {@code pickupTime} and {@code scheduleNotes}
     *             are additionally required when {@code action = "accept"}
     * @param resp the {@link HttpServletResponse} used to issue the redirect
     * @throws ServletException if an unexpected servlet error occurs
     * @throws IOException      if an I/O error occurs during redirect
     */

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        User user   = (User) req.getSession().getAttribute("user");
        String action = req.getParameter("action");

        // Parse and validate the requestId parameter
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
                // Delegate to service: validates pickup time, creates schedule,
                // updates request status, and sends NGO notification
                error = requestService.acceptRequest(
                        requestId, user.getId(),
                        req.getParameter("pickupTime"),
                        req.getParameter("scheduleNotes")
                );
                if (error == null) successMsg = "Request accepted and pickup scheduled.";
            }
            case "reject" -> {
                // Delegate to service: updates request to REJECTED, reverts food
                // item to available, and sends NGO notification
                error = requestService.rejectRequest(requestId, user.getId());
                if (error == null) successMsg = "Request rejected.";
            }
            case "complete" -> {
                // Delegate to service: transitions request and food item to
                // COMPLETED and sends NGO notification
                error = requestService.completeRequest(requestId, user.getId());
                if (error == null) successMsg = "Request marked as completed.";
            }
            default -> error = "Unknown action.";
        }

        // Post-Redirect-Get: carry feedback via URL query parameters
        String redirect = req.getContextPath() + "/donor/requests?";
        if (error != null) {
            redirect += "error=" + java.net.URLEncoder.encode(error, "UTF-8");
        } else {
            redirect += "success=" + java.net.URLEncoder.encode(successMsg, "UTF-8");
        }
        resp.sendRedirect(redirect);
    }
}
