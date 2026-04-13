<%@ page import="model.User" %>
<%@ page import="service.NotificationService" %>
<%
    User navUser = (User) session.getAttribute("user");
    int unread = 0;
    if (navUser != null) {
        try {
            unread = new NotificationService().getUnreadCount(navUser.getId());
        } catch (Exception ignored) {}
    }
    String ctx = request.getContextPath();
    String uri = request.getRequestURI();
%>
<nav class="navbar">
    <div class="navbar-inner">
        <a href="<%= ctx %>/" class="navbar-brand">
            <span class="brand-icon">🌿</span> FoodShare
        </a>

        <% if (navUser != null) { %>
        <button class="nav-toggle" aria-label="Toggle menu" aria-expanded="false">
            <span></span><span></span><span></span>
        </button>
        <div class="nav-links">

            <% if ("donor".equals(navUser.getRole())) { %>
            <a href="<%= ctx %>/donor/dashboard"  <%= uri.contains("/donor/dashboard")  ? "class='active'" : "" %>>Dashboard</a>
            <a href="<%= ctx %>/donor/myListings" <%= uri.contains("/donor/myListings") ? "class='active'" : "" %>>My Listings</a>
            <a href="<%= ctx %>/donor/addFood"    <%= uri.contains("/donor/addFood")    ? "class='active'" : "" %>>+ Add Food</a>
            <a href="<%= ctx %>/donor/requests"   <%= uri.contains("/donor/requests")   ? "class='active'" : "" %>>
                Requests <% if (unread > 0) { %><span class="nav-badge"><%= unread %></span><% } %>
            </a>
            <% } else if ("ngo".equals(navUser.getRole())) { %>
            <a href="<%= ctx %>/ngo/dashboard"  <%= uri.contains("/ngo/dashboard")  ? "class='active'" : "" %>>Dashboard</a>
            <a href="<%= ctx %>/ngo/searchFood" <%= uri.contains("/ngo/searchFood") ? "class='active'" : "" %>>Find Food</a>
            <a href="<%= ctx %>/ngo/myRequests" <%= uri.contains("/ngo/myRequests") ? "class='active'" : "" %>>My Requests</a>
            <% if (unread > 0) { %>
            <a href="<%= ctx %>/ngo/dashboard"><span class="nav-badge" style="width:auto;padding:.2em .6em;border-radius:999px;"><%= unread %> new</span></a>
            <% } %>
            <% } else if ("admin".equals(navUser.getRole())) { %>
            <a href="<%= ctx %>/admin/dashboard"   <%= uri.contains("/admin/dashboard")   ? "class='active'" : "" %>>Dashboard</a>
            <a href="<%= ctx %>/admin/manageUsers" <%= uri.contains("/admin/manageUsers") ? "class='active'" : "" %>>Users</a>
            <a href="<%= ctx %>/admin/manageFood"  <%= uri.contains("/admin/manageFood")  ? "class='active'" : "" %>>Food</a>
            <a href="<%= ctx %>/admin/reports"     <%= uri.contains("/admin/reports")     ? "class='active'" : "" %>>Reports</a>
            <% } %>

            <a href="<%= ctx %>/about"   <%= uri.contains("/about")   ? "class='active'" : "" %>>About</a>
            <a href="<%= ctx %>/profile" <%= uri.contains("/profile")  ? "class='active'" : "" %>>👤 <%= navUser.getName().split(" ")[0] %></a>
            <a href="<%= ctx %>/logout">Logout</a>
        </div>
        <% } else { %>
        <div class="nav-links">
            <a href="<%= ctx %>/login">Login</a>
            <a href="<%= ctx %>/register">Register</a>
            <a href="<%= ctx %>/about">About</a>
        </div>
        <% } %>
    </div>
</nav>
