<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.User, java.util.List" %>
<%
    String ctx = request.getContextPath();
    List<User> pendingNgos = (List<User>) request.getAttribute("pendingNgos");

    int  totalDonors   = request.getAttribute("totalDonors")    != null ? (int)    request.getAttribute("totalDonors")    : 0;
    int  totalNgos     = request.getAttribute("totalNgos")      != null ? (int)    request.getAttribute("totalNgos")      : 0;
    int  totalFood     = request.getAttribute("totalFood")      != null ? (int)    request.getAttribute("totalFood")      : 0;
    int  totalRequests = request.getAttribute("totalRequests")  != null ? (int)    request.getAttribute("totalRequests")  : 0;
    int  completed     = request.getAttribute("completedCount") != null ? (int)    request.getAttribute("completedCount") : 0;
    int  pending       = request.getAttribute("pendingCount")   != null ? (int)    request.getAttribute("pendingCount")   : 0;
    double foodSaved   = request.getAttribute("foodSaved")      != null ? (double) request.getAttribute("foodSaved")      : 0.0;
    String topDonor    = request.getAttribute("topDonor")       != null ? (String) request.getAttribute("topDonor")       : "N/A";
    String mostReq     = request.getAttribute("mostRequested")  != null ? (String) request.getAttribute("mostRequested")  : "N/A";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard – FoodShare</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
<%@ include file="../common/navbar.jsp" %>
<div class="page-wrapper">
    <div class="page-header">
        <h1>🛡️ Admin Dashboard</h1>
        <p>Platform overview and management controls.</p>
    </div>

    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-error"><span class="alert-icon">⚠️</span> <%= request.getAttribute("error") %></div>
    <% } %>

    <!-- Key Stats -->
    <div class="stats-grid">
        <div class="stat-card">
            <span class="stat-label">Total Donors</span>
            <span class="stat-value"><%= totalDonors %></span>
        </div>
        <div class="stat-card blue">
            <span class="stat-label">Total NGOs</span>
            <span class="stat-value"><%= totalNgos %></span>
        </div>
        <div class="stat-card orange">
            <span class="stat-label">Food Listed</span>
            <span class="stat-value"><%= totalFood %></span>
        </div>
        <div class="stat-card">
            <span class="stat-label">Total Requests</span>
            <span class="stat-value"><%= totalRequests %></span>
        </div>
        <div class="stat-card blue">
            <span class="stat-label">Completed</span>
            <span class="stat-value"><%= completed %></span>
        </div>
        <div class="stat-card orange">
            <span class="stat-label">Pending</span>
            <span class="stat-value"><%= pending %></span>
        </div>
        <div class="stat-card">
            <span class="stat-label">Food Saved (kg)</span>
            <span class="stat-value"><%= String.format("%.1f", foodSaved) %></span>
        </div>
        <div class="stat-card">
            <span class="stat-label">Pending NGOs</span>
            <span class="stat-value"><%= pendingNgos != null ? pendingNgos.size() : 0 %></span>
        </div>
    </div>

    <!-- Highlights -->
    <div style="display:grid;grid-template-columns:1fr 1fr;gap:1rem;margin-bottom:1.5rem;">
        <div class="card" style="background:linear-gradient(135deg,#e8f5e9,#f1f8f1);border-left:4px solid var(--green-light);">
            <div class="text-muted text-small" style="text-transform:uppercase;letter-spacing:.05em;">🏆 Top Donor</div>
            <div style="font-size:1.3rem;font-weight:700;color:var(--green-dark);margin-top:.25rem;"><%= topDonor %></div>
        </div>
        <div class="card" style="background:linear-gradient(135deg,#e3f2fd,#f5f5f5);border-left:4px solid var(--blue);">
            <div class="text-muted text-small" style="text-transform:uppercase;letter-spacing:.05em;">🍽️ Most Requested Food</div>
            <div style="font-size:1.3rem;font-weight:700;color:var(--blue);margin-top:.25rem;"><%= mostReq %></div>
        </div>
    </div>

    <!-- Pending NGO Approvals -->
    <div class="card mb-2">
        <div class="card-header">
            <h2 class="card-title">⏳ Pending NGO Approvals
                <% if (pendingNgos != null && !pendingNgos.isEmpty()) { %>
                <span class="nav-badge" style="width:auto;padding:.2em .6em;border-radius:999px;"><%= pendingNgos.size() %></span>
                <% } %>
            </h2>
            <a href="<%= ctx %>/admin/manageUsers" class="btn btn-secondary btn-sm">Manage All Users</a>
        </div>
        <% if (pendingNgos == null || pendingNgos.isEmpty()) { %>
        <p class="text-muted text-small">No NGOs awaiting approval.</p>
        <% } else { %>
        <div class="table-wrapper">
            <table>
                <thead>
                <tr><th>Name</th><th>Email</th><th>Phone</th><th>Registered</th><th>Actions</th></tr>
                </thead>
                <tbody>
                <% for (User ngo : pendingNgos) { %>
                <tr>
                    <td><strong><%= ngo.getName() %></strong></td>
                    <td><%= ngo.getEmail() %></td>
                    <td><%= ngo.getPhone() != null ? ngo.getPhone() : "–" %></td>
                    <td class="text-small"><%= ngo.getCreatedAt() != null ? ngo.getCreatedAt().toLocalDate() : "" %></td>
                    <td>
                        <form action="<%= ctx %>/admin/manageUsers" method="post" style="display:inline">
                            <input type="hidden" name="action" value="approve">
                            <input type="hidden" name="userId" value="<%= ngo.getId() %>">
                            <button type="submit" class="btn btn-primary btn-sm">✅ Approve</button>
                        </form>
                        <form action="<%= ctx %>/admin/manageUsers" method="post" style="display:inline"
                              data-confirm="Reject this NGO application?">
                            <input type="hidden" name="action" value="reject">
                            <input type="hidden" name="userId" value="<%= ngo.getId() %>">
                            <button type="submit" class="btn btn-danger btn-sm">❌ Reject</button>
                        </form>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
        <% } %>
    </div>

    <!-- Quick Actions -->
    <div class="card">
        <div class="card-header"><h2 class="card-title">Quick Actions</h2></div>
        <div class="btn-group">
            <a href="<%= ctx %>/admin/manageUsers" class="btn btn-primary">👥 Manage Users</a>
            <a href="<%= ctx %>/admin/manageFood"  class="btn btn-secondary">🍽️ Manage Food</a>
            <a href="<%= ctx %>/admin/reports"     class="btn btn-info">📊 View Reports</a>
        </div>
    </div>
</div>
<jsp:include page="../common/footer.jsp" />
<script src="<%= ctx %>/js/script.js"></script>
</body>
</html>
