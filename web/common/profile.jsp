<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.User" %>
<%
    User u = (User) session.getAttribute("user");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile – FoodShare</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="page-wrapper">
    <div class="page-header">
        <h1>👤 My Profile</h1>
        <p>Update your personal details or change your password.</p>
    </div>

    <% if (request.getAttribute("success") != null) { %>
    <div class="alert alert-success"><span class="alert-icon">✅</span> <%= request.getAttribute("success") %></div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-error"><span class="alert-icon">⚠️</span> <%= request.getAttribute("error") %></div>
    <% } %>

    <div style="display:grid;grid-template-columns:1fr 1fr;gap:1.5rem;max-width:860px;">

        <!-- Update Profile -->
        <div class="card">
            <div class="card-header"><h2 class="card-title">Account Details</h2></div>
            <form action="<%= ctx %>/profile" method="post">
                <input type="hidden" name="mode" value="updateProfile">
                <div class="form-group">
                    <label>Role</label>
                    <input type="text" value="<%= u.getRole().toUpperCase() %>" disabled>
                </div>
                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" value="<%= u.getEmail() %>" disabled>
                    <span class="form-hint">Email cannot be changed.</span>
                </div>
                <div class="form-group">
                    <label for="name">Name <span class="required">*</span></label>
                    <input type="text" id="name" name="name" required
                           value="<%= u.getName() %>" maxlength="100">
                </div>
                <div class="form-group">
                    <label for="phone">Phone</label>
                    <input type="text" id="phone" name="phone" value="<%= u.getPhone() != null ? u.getPhone() : "" %>">
                </div>
                <div class="form-group">
                    <label for="address">Address</label>
                    <input type="text" id="address" name="address" value="<%= u.getAddress() != null ? u.getAddress() : "" %>">
                </div>
                <button type="submit" class="btn btn-primary">Save Changes</button>
            </form>
        </div>

        <!-- Change Password -->
        <div class="card">
            <div class="card-header"><h2 class="card-title">Change Password</h2></div>
            <form action="<%= ctx %>/profile" method="post">
                <input type="hidden" name="mode" value="changePassword">
                <div class="form-group">
                    <label for="currentPassword">Current Password <span class="required">*</span></label>
                    <input type="password" id="currentPassword" name="currentPassword" required>
                </div>
                <div class="form-group">
                    <label for="password">New Password <span class="required">*</span></label>
                    <input type="password" id="password" name="newPassword" required minlength="8">
                    <span id="pwd-strength"></span>
                </div>
                <div class="form-group">
                    <label for="confirmPassword">Confirm New Password <span class="required">*</span></label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required>
                    <span id="confirm-hint" class="form-hint"></span>
                </div>
                <button type="submit" class="btn btn-warning">Change Password</button>
            </form>
        </div>

    </div>

    <div class="mt-2" style="max-width:860px;">
        <div class="card">
            <p class="text-small text-muted">
                <strong>Member since:</strong>
                <%= u.getCreatedAt() != null ? u.getCreatedAt().toLocalDate() : "N/A" %>
                &nbsp;|&nbsp;
                <strong>Account status:</strong>
                <%= u.isApproved() ? "✅ Approved" : "⏳ Pending Approval" %>
            </p>
        </div>
    </div>
</div>

<footer><p>&copy; 2025 FoodShare</p></footer>
<script src="<%= ctx %>/js/script.js"></script>
</body>
</html>
