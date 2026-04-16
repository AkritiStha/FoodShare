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
    String navCtx = request.getContextPath();
    String navUri = request.getRequestURI();
%>
<nav class="navbar">
    <div class="navbar-inner">
        <a href="<%= navCtx %>/" class="navbar-brand">
            <span class="brand-icon">🌿</span> FoodShare
        </a>

        <% if (navUser != null) { %>
        <button class="nav-toggle" aria-label="Toggle menu" aria-expanded="false">
            <span></span><span></span><span></span>
        </button>
        <div class="nav-links">

            <% if ("donor".equals(navUser.getRole())) { %>
            <a href="<%= navCtx %>/donor/dashboard"  <%= navUri.contains("/donor/dashboard")  ? "class='active'" : "" %>>Dashboard</a>
            <a href="<%= navCtx %>/donor/myListings" <%= navUri.contains("/donor/myListings") ? "class='active'" : "" %>>My Listings</a>
            <a href="<%= navCtx %>/donor/addFood"    <%= navUri.contains("/donor/addFood")    ? "class='active'" : "" %>>+ Add Food</a>
            <a href="<%= navCtx %>/donor/requests"   <%= navUri.contains("/donor/requests")   ? "class='active'" : "" %>>
                Requests <% if (unread > 0) { %><span class="nav-badge"><%= unread %></span><% } %>
            </a>
            <% } else if ("ngo".equals(navUser.getRole())) { %>
            <a href="<%= navCtx %>/ngo/dashboard"  <%= navUri.contains("/ngo/dashboard")  ? "class='active'" : "" %>>Dashboard</a>
            <a href="<%= navCtx %>/ngo/searchFood" <%= navUri.contains("/ngo/searchFood") ? "class='active'" : "" %>>Find Food</a>
            <a href="<%= navCtx %>/ngo/myRequests" <%= navUri.contains("/ngo/myRequests") ? "class='active'" : "" %>>My Requests</a>
            <% if (unread > 0) { %>
            <a href="<%= navCtx %>/ngo/dashboard"><span class="nav-badge" style="width:auto;padding:.2em .6em;border-radius:999px;"><%= unread %> new</span></a>
            <% } %>
            <% } else if ("admin".equals(navUser.getRole())) { %>
            <a href="<%= navCtx %>/admin/dashboard"   <%= navUri.contains("/admin/dashboard")   ? "class='active'" : "" %>>Dashboard</a>
            <a href="<%= navCtx %>/admin/manageUsers" <%= navUri.contains("/admin/manageUsers") ? "class='active'" : "" %>>Users</a>
            <a href="<%= navCtx %>/admin/manageFood"  <%= navUri.contains("/admin/manageFood")  ? "class='active'" : "" %>>Food</a>
            <a href="<%= navCtx %>/admin/reports"     <%= navUri.contains("/admin/reports")     ? "class='active'" : "" %>>Reports</a>
            <% } %>

            <a href="<%= navCtx %>/about"   <%= navUri.contains("/about")   ? "class='active'" : "" %>>About</a>
            <a href="<%= navCtx %>/profile" <%= navUri.contains("/profile")  ? "class='active'" : "" %>>👤 <%= navUser.getName().split(" ")[0] %></a>
            <a href="<%= navCtx %>/logout">Logout</a>
        </div>
        <% } else { %>
        <div class="nav-links">
            <a href="<%= navCtx %>/login">Login</a>
            <a href="<%= navCtx %>/register">Register</a>
            <a href="<%= navCtx %>/about">About</a>
        </div>
        <% } %>
    </div>
</nav>
