<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.User" %>
<%
    if (session.getAttribute("user") != null) {
        response.sendRedirect(request.getContextPath() + "/");
        return;
    }
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login – FoodShare</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
<%@ include file="navbar.jsp" %>

<div class="auth-page">
    <div class="auth-card">
        <div class="auth-logo">
            <div class="auth-logo-icon">🌿</div>
            <h1>FoodShare</h1>
            <p>Reducing food waste, together.</p>
        </div>

        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error">
            <span class="alert-icon">⚠️</span> <%= request.getAttribute("error") %>
        </div>
        <% } %>
        <% if (request.getAttribute("success") != null) { %>
        <div class="alert alert-success">
            <span class="alert-icon">✅</span> <%= request.getAttribute("success") %>
        </div>
        <% } %>

        <form action="<%= ctx %>/login" method="post" novalidate>
            <div class="form-group">
                <label for="email">Email Address <span class="required">*</span></label>
                <input type="email" id="email" name="email" required
                       placeholder="you@example.com" autocomplete="email">
            </div>
            <div class="form-group">
                <label for="password">Password <span class="required">*</span></label>
                <input type="password" id="password" name="password" required
                       placeholder="Your password" autocomplete="current-password">
            </div>
            <button type="submit" class="btn btn-primary btn-block btn-lg" style="margin-top:.5rem">
                Sign In
            </button>
        </form>

        <div class="form-divider">or</div>
        <p class="text-center text-small">
            Don't have an account? <a href="<%= ctx %>/register">Register here</a>
        </p>
        <p class="text-center text-small mt-1">
            <a href="<%= ctx %>/about">About FoodShare</a>
        </p>
    </div>
</div>

<footer><p>&copy; 2025 FoodShare – CS5054NT Coursework</p></footer>
<script src="<%= ctx %>/js/script.js"></script>
</body>
</html>
