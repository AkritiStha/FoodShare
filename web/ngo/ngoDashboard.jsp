<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.User, model.Request, model.Notification, java.util.List" %>
<%
    User u = (User) session.getAttribute("user");
    List<Request>      myRequests  = (List<Request>)      request.getAttribute("myRequests");
    List<Notification> notifs      = (List<Notification>) request.getAttribute("notifications");
    int unreadCount   = request.getAttribute("unreadCount") != null ? (int) request.getAttribute("unreadCount") : 0;
    int availableCount= request.getAttribute("availableCount") != null ? (int) request.getAttribute("availableCount") : 0;
    String ctx = request.getContextPath();

    long pendingCnt   = myRequests == null ? 0 : myRequests.stream().filter(r->"PENDING".equals(r.getStatus())).count();
    long acceptedCnt  = myRequests == null ? 0 : myRequests.stream().filter(r->"ACCEPTED".equals(r.getStatus())).count();
    long completedCnt = myRequests == null ? 0 : myRequests.stream().filter(r->"COMPLETED".equals(r.getStatus())).count();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>NGO Dashboard – FoodShare</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
<%@ include file="../common/navbar.jsp" %>
<div class="page-wrapper">
    <div class="page-header">
        <h1>Welcome, <%= u.getName() %> 🤝</h1>
        <p>Find available food and track your requests.</p>
    </div>

    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-error"><span class="alert-icon">⚠️</span> <%= request.getAttribute("error") %></div>
    <% } %>

    <!-- Stats -->
    <div class="stats-grid">
        <div class="stat-card blue">
            <span class="stat-label">Available Food</span>
            <span class="stat-value"><%= availableCount %></span>
        </div>
        <div class="stat-card orange">
            <span class="stat-label">Pending Requests</span>
            <span class="stat-value"><%= pendingCnt %></span>
        </div>
        <div class="stat-card">
            <span class="stat-label">Accepted</span>
            <span class="stat-value"><%= acceptedCnt %></span>
        </div>
        <div class="stat-card blue">
            <span class="stat-label">Completed</span>
            <span class="stat-value"><%= completedCnt %></span>
        </div>
    </div>

    <div style="display:grid;grid-template-columns:2fr 1fr;gap:1.5rem;align-items:start;">

        <!-- Recent requests -->
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">📋 My Recent Requests</h2>
                <a href="<%= ctx %>/ngo/myRequests" class="btn btn-secondary btn-sm">View All</a>
            </div>
            <% if (myRequests == null || myRequests.isEmpty()) { %>
            <div class="empty-state">
                <div class="empty-state-icon">📭</div>
                <p class="empty-state-text">You haven't made any requests yet.</p>
                <a href="<%= ctx %>/ngo/searchFood" class="btn btn-primary mt-2">Find Food Now</a>
            </div>
            <% } else { %>
            <div class="table-wrapper">
                <table>
                    <thead>
                    <tr><th>Food</th><th>Donor</th><th>Status</th><th>Pickup</th></tr>
                    </thead>
                    <tbody>
                    <% int shown = 0; for (Request r : myRequests) { if (shown++ >= 5) break; %>
                    <tr>
                        <td><strong><%= r.getFoodItemName() %></strong></td>
                        <td><%= r.getDonorName() %></td>
                        <td><span class="badge badge-<%= r.getStatus().toLowerCase() %>"><%= r.getStatus() %></span></td>
                        <td class="text-small">
                            <%= r.getPickupTime() != null ? r.getPickupTime().toLocalDate().toString() : "–" %>
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

    <div class="card mt-2">
        <div class="card-header"><h2 class="card-title">Quick Actions</h2></div>
        <div class="btn-group">
            <a href="<%= ctx %>/ngo/searchFood" class="btn btn-primary">🔍 Find Available Food</a>
            <a href="<%= ctx %>/ngo/myRequests" class="btn btn-secondary">📋 My Requests</a>
            <a href="<%= ctx %>/profile"        class="btn btn-secondary">👤 My Profile</a>
        </div>
    </div>
</div>
<jsp:include page="../common/footer.jsp" />
<script src="<%= ctx %>/js/script.js"></script>
</body>
</html>
