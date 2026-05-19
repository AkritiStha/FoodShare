<%@ page import="model.User" %>
<%@ page import="service.NotificationService" %>
<%
    User navUser = (User) session.getAttribute("user");
    int unread = 0;
    if (navUser != null) {
        try { unread = new NotificationService().getUnreadCount(navUser.getId()); } catch (Exception ignored) {}
    }
    String navCtx = request.getContextPath();
    String navUri = request.getRequestURI();
    String firstInitial = (navUser != null && navUser.getName() != null && !navUser.getName().isEmpty())
            ? String.valueOf(navUser.getName().charAt(0)).toUpperCase() : "U";
    String firstName = "";
    if (navUser != null && navUser.getName() != null) {
        String[] parts = navUser.getName().split(" ");
        firstName = parts[0];
    }
%>
<nav class="fs-navbar">
    <div class="fs-nav-inner">

        <%-- Logo --%>
        <a href="<%= navCtx %>/" class="fs-logo">
            <div class="fs-logo-mark" aria-hidden="true">
                <svg viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg" style="fill:white;width:18px;height:18px;">
                    <path d="M10 2 C7 5 5 8 6.5 11.5 C7.5 14 12.5 14 13.5 11.5 C15 8 13 5 10 2Z"/>
                    <path d="M10 14 L10 18" style="stroke:white;stroke-width:1.5;stroke-linecap:round;fill:none;"/>
                </svg>
            </div>
            FoodShare
        </a>

        <%-- Mobile Toggle --%>
        <button class="fs-nav-toggle" id="fsNavToggle" aria-label="Toggle navigation" aria-expanded="false">
            <span></span><span></span><span></span>
        </button>

        <%-- Links --%>
        <ul class="fs-nav-links" id="fsNavLinks">
            <li>
                <a href="<%= navCtx %>/"
                   class="<%= (navUri.endsWith("/") || navUri.contains("index.jsp")) ? "active" : "" %>">
                    Home
                </a>
            </li>

            <% if (navUser == null || "donor".equals(navUser.getRole())) { %>
            <li>
                <a href="<%= navCtx %>/donor/addFood"
                   class="<%= navUri.contains("/donor/addFood") ? "active" : "" %>">
                    Donate
                </a>
            </li>
            <% } %>

            <% if (navUser == null || "ngo".equals(navUser.getRole())) { %>
            <li>
                <a href="<%= navCtx %>/ngo/searchFood"
                   class="<%= navUri.contains("/ngo/searchFood") ? "active" : "" %>">
                    Browse Food
                </a>
            </li>
            <% } %>

            <li>
                <a href="<%= navCtx %>/about"
                   class="<%= navUri.contains("/about") ? "active" : "" %>">
                    About
                </a>
            </li>

            <li>
                <a href="<%= navCtx %>/contact"
                   class="<%= navUri.contains("/contact") ? "active" : "" %>">
                    Contact
                </a>
            </li>

            <li class="fs-nav-sep" role="separator" aria-hidden="true"></li>

            <% if (navUser != null) { %>

            <li>
                <% if ("donor".equals(navUser.getRole())) { %>
                <a href="<%= navCtx %>/donor/dashboard"
                   class="<%= navUri.contains("/donor/dashboard") ? "active" : "" %>">
                    Dashboard
                </a>
                <% } else if ("ngo".equals(navUser.getRole())) { %>
                <a href="<%= navCtx %>/ngo/dashboard"
                   class="<%= navUri.contains("/ngo/dashboard") ? "active" : "" %>">
                    Dashboard
                </a>
                <% } else if ("admin".equals(navUser.getRole())) { %>
                <a href="<%= navCtx %>/admin/dashboard"
                   class="<%= navUri.contains("/admin/dashboard") ? "active" : "" %>">
                    Dashboard
                </a>
                <% } %>
            </li>

            <li>
                <a href="<%= navCtx %>/profile" class="fs-user-label">
                    <span class="fs-avatar"><%= firstInitial %></span>
                    <%= firstName %>
                    <% if (unread > 0) { %>
                    <span class="fs-badge"><%= unread > 9 ? "9+" : unread %></span>
                    <% } %>
                </a>
            </li>

            <li>
                <a href="<%= navCtx %>/logout" class="fs-btn-ghost">Sign out</a>
            </li>

            <% } else { %>
            <li><a href="<%= navCtx %>/login" class="fs-btn-ghost">Log in</a></li>
            <li><a href="<%= navCtx %>/register" class="fs-btn-solid" style="color: white;">Get started</a></li>
            <% } %>
        </ul>
    </div>
</nav>

<script>
    (function () {
        var toggle = document.getElementById('fsNavToggle');
        var links  = document.getElementById('fsNavLinks');
        if (!toggle || !links) return;
        toggle.addEventListener('click', function () {
            var open = links.classList.toggle('open');
            toggle.setAttribute('aria-expanded', String(open));
        });
    })();
</script>
