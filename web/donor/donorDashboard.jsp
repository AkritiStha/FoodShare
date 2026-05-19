<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.User, model.FoodItem, model.Request, model.Notification, java.util.List" %>
<%
    User u             = (User) session.getAttribute("user");
    List<FoodItem> listings    = (List<FoodItem>) request.getAttribute("myListings");
    List<Request>  pending     = (List<Request>)  request.getAttribute("pendingRequests");
    List<Notification> notifs  = (List<Notification>) request.getAttribute("notifications");
    int unreadCount            = request.getAttribute("unreadCount") != null
            ? (int) request.getAttribute("unreadCount") : 0;
    String ctx = request.getContextPath();

    long available  = listings == null ? 0 : listings.stream().filter(f->"available".equals(f.getStatus())).count();
    long requested  = listings == null ? 0 : listings.stream().filter(f->"requested".equals(f.getStatus())).count();
    long completed  = listings == null ? 0 : listings.stream().filter(f->"completed".equals(f.getStatus())).count();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Donor Dashboard – FoodShare</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
<%@ include file="../common/navbar.jsp" %>
<div class="page-wrapper">
    <div class="page-header">
        <h1>Welcome back, <%= u.getName() %> 👋</h1>
        <p>Manage your food donations and incoming requests.</p>
    </div>

    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-error"><span class="alert-icon">⚠️</span> <%= request.getAttribute("error") %></div>
    <% } %>

    <!-- Stats -->
    <div class="stats-grid">
        <div class="stat-card">
            <span class="stat-label">Total Listings</span>
            <span class="stat-value"><%= listings == null ? 0 : listings.size() %></span>
        </div>
        <div class="stat-card">
            <span class="stat-label">Available</span>
            <span class="stat-value"><%= available %></span>
        </div>
        <div class="stat-card orange">
            <span class="stat-label">Requested</span>
            <span class="stat-value"><%= requested %></span>
        </div>
        <div class="stat-card blue">
            <span class="stat-label">Completed</span>
            <span class="stat-value"><%= completed %></span>
        </div>
    </div>

    <div style="display:grid;grid-template-columns:2fr 1fr;gap:1.5rem;align-items:start;">

        <!-- Pending Requests -->
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">🔔 Pending Requests</h2>
                <a href="<%= ctx %>/donor/requests" class="btn btn-secondary btn-sm">View All</a>
            </div>
            <% if (pending == null || pending.isEmpty()) { %>
            <div class="empty-state">
                <div class="empty-state-icon">📭</div>
                <p class="empty-state-text">No pending requests at the moment.</p>
            </div>
            <% } else { %>
            <div class="table-wrapper">
                <table>
                    <thead>
                    <tr><th>Food Item</th><th>From NGO</th><th>Date</th><th>Action</th></tr>
                    </thead>
                    <tbody>
                    <% for (Request r : pending) { %>
                    <tr>
                        <td><strong><%= r.getFoodItemName() %></strong></td>
                        <td><%= r.getNgoName() %></td>
                        <td class="text-small text-muted"><%= r.getCreatedAt() != null ? r.getCreatedAt().toLocalDate() : "" %></td>
                        <td>
                            <button class="btn btn-primary btn-sm" onclick="openAcceptModal(<%= r.getId() %>)">Accept</button>
                            <form action="<%= ctx %>/donor/requests" method="post" style="display:inline" data-confirm="Reject this request?">
                                <input type="hidden" name="action"    value="reject">
                                <input type="hidden" name="requestId" value="<%= r.getId() %>">
                                <button type="submit" class="btn btn-danger btn-sm">Reject</button>
                            </form>
                        </td>
                    </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
            <% } %>
        </div>

        <!-- Notifications -->
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">🔔 Notifications
                    <% if (unreadCount > 0) { %><span class="nav-badge"><%= unreadCount %></span><% } %>
                </h2>
            </div>
            <% if (notifs == null || notifs.isEmpty()) { %>
            <p class="text-muted text-small">No notifications yet.</p>
            <% } else { %>
            <div class="notification-list">
                <% for (Notification n : notifs) { %>
                <div class="notification-item <%= n.isRead() ? "" : "unread" %>">
                    <span class="notif-icon"><%= n.isRead() ? "📩" : "🔔" %></span>
                    <div class="notif-body">
                        <div><%= n.getMessage() %></div>
                        <div class="notif-time"><%= n.getCreatedAt() != null ? n.getCreatedAt().toLocalDate() : "" %></div>
                    </div>
                </div>
                <% } %>
            </div>
            <% } %>
        </div>

    </div>

    <!-- Quick actions -->
    <div class="card mt-2">
        <div class="card-header"><h2 class="card-title">Quick Actions</h2></div>
        <div class="btn-group">
            <a href="<%= ctx %>/donor/addFood"    class="btn btn-primary">+ Add Food Listing</a>
            <a href="<%= ctx %>/donor/myListings" class="btn btn-secondary">📋 My Listings</a>
            <a href="<%= ctx %>/donor/requests"   class="btn btn-secondary">📥 All Requests</a>
            <a href="<%= ctx %>/profile"          class="btn btn-secondary">👤 My Profile</a>
        </div>
    </div>
</div>

<!-- Accept Modal -->
<div class="modal-overlay" id="acceptModal">
    <div class="modal-box">
        <div class="modal-header">
            <h3 class="modal-title">Accept Request & Schedule Pickup</h3>
            <button class="modal-close" onclick="closeModal('acceptModal')">×</button>
        </div>
        <form action="<%= ctx %>/donor/requests" method="post">
            <input type="hidden" name="action"    value="accept">
            <input type="hidden" name="requestId" id="accept-request-id" value="">
            <div class="form-group">
                <label for="pickupTime">Pickup Date &amp; Time <span class="required">*</span></label>
                <input type="datetime-local" id="pickupTime" name="pickupTime" required>
            </div>
            <div class="form-group">
                <label for="scheduleNotes">Notes for NGO</label>
                <textarea id="scheduleNotes" name="scheduleNotes" placeholder="e.g. Please bring your own containers"></textarea>
            </div>
            <div class="btn-group">
                <button type="submit" class="btn btn-primary">Confirm Acceptance</button>
                <button type="button" class="btn btn-secondary" onclick="closeModal('acceptModal')">Cancel</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
<script src="<%= ctx %>/js/script.js"></script>
</body>
</html>
