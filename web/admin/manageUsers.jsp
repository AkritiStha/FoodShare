<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.User, java.util.List" %>
<%
    List<User> allUsers    = (List<User>) request.getAttribute("allUsers");
    List<User> pendingNgos = (List<User>) request.getAttribute("pendingNgos");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Users – FoodShare</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>

<body>
<%@ include file="../common/navbar.jsp" %>
<div class="page-wrapper">
    <div class="page-header">
        <h1>👥 Manage Users</h1>
        <p>Approve NGOs, view all accounts, and remove problematic users.</p>
    </div>

    <% if (request.getAttribute("success") != null) { %>
    <div class="alert alert-success"><span class="alert-icon">✅</span> <%= request.getAttribute("success") %></div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-error"><span class="alert-icon">⚠️</span> <%= request.getAttribute("error") %></div>
    <% } %>

    <!-- Pending NGOs first -->
    <% if (pendingNgos != null && !pendingNgos.isEmpty()) { %>
    <div class="alert alert-warning">
        <span class="alert-icon">⚠️</span>
        <strong><%= pendingNgos.size() %> NGO(s)</strong> are awaiting your approval.
    </div>
    <% } %>

    <!-- Filter tabs (simple CSS-based, no JS state) -->
    <div style="display:flex;gap:.5rem;flex-wrap:wrap;margin-bottom:1rem;">
        <span class="btn btn-secondary btn-sm" style="cursor:default;">All Users (<%= allUsers != null ? allUsers.size() : 0 %>)</span>
    </div>

    <div class="table-wrapper">
        <table>
            <thead>
            <tr>
                <th>#</th><th>Name</th><th>Email</th><th>Role</th>
                <th>Phone</th><th>Status</th><th>Registered</th><th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <% if (allUsers != null) { for (User u : allUsers) { %>
            <tr>
                <td class="text-muted text-small"><%= u.getId() %></td>
                <td><strong><%= u.getName() %></strong></td>
                <td class="text-small"><%= u.getEmail() %></td>
                <td><span class="badge badge-<%= u.getRole() %>"><%= u.getRole().toUpperCase() %></span></td>
                <td class="text-small"><%= u.getPhone() != null ? u.getPhone() : "–" %></td>
                <td>
                    <% if ("admin".equals(u.getRole())) { %>
                    <span class="badge badge-admin">ADMIN</span>
                    <% } else if (u.isApproved()) { %>
                    <span class="badge badge-approved">Approved</span>
                    <% } else { %>
                    <span class="badge badge-unapproved">Pending</span>
                    <% } %>
                </td>
                <td class="text-small"><%= u.getCreatedAt() != null ? u.getCreatedAt().toLocalDate() : "" %></td>
                <td>
                    <% if ("ngo".equals(u.getRole()) && !u.isApproved()) { %>
                    <form action="<%= ctx %>/admin/manageUsers" method="post" style="display:inline">
                        <input type="hidden" name="action" value="approve">
                        <input type="hidden" name="userId" value="<%= u.getId() %>">
                        <button type="submit" class="btn btn-primary btn-sm">✅ Approve</button>
                    </form>
                    <% } %>
                    <% if (!"admin".equals(u.getRole())) { %>
                    <form action="<%= ctx %>/admin/manageUsers" method="post" style="display:inline"
                          data-confirm="Delete user '<%= u.getName() %>'? This cannot be undone.">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="userId" value="<%= u.getId() %>">
                        <button type="submit" class="btn btn-danger btn-sm">🗑️</button>
                    </form>
                    <% } %>
                </td>
            </tr>
            <% } } %>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
<script src="<%= ctx %>/js/script.js"></script>
</body>
</html>
