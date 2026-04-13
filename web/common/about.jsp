<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.User" %>
<%  String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>About – FoodShare</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="page-wrapper">

    <div class="about-hero">
        <h1>🌿 About FoodShare</h1>
        <p>A platform that connects food donors with NGOs and shelters to reduce food waste and feed communities in need.</p>
    </div>

    <div class="features-grid">
        <div class="feature-card">
            <div class="feature-icon">🍽️</div>
            <div class="feature-title">Donors</div>
            <p class="text-small text-muted">Restaurants, hotels and individuals list surplus food so it reaches people rather than landfill.</p>
        </div>
        <div class="feature-card">
            <div class="feature-icon">🤝</div>
            <div class="feature-title">NGOs &amp; Shelters</div>
            <p class="text-small text-muted">Verified organisations search for available food near them and request pickups in seconds.</p>
        </div>
        <div class="feature-card">
            <div class="feature-icon">📍</div>
            <div class="feature-title">Location Matching</div>
            <p class="text-small text-muted">Our Haversine distance algorithm surfaces the nearest food first, minimising transport time.</p>
        </div>
        <div class="feature-card">
            <div class="feature-icon">📊</div>
            <div class="feature-title">Impact Tracking</div>
            <p class="text-small text-muted">Admins monitor total food saved, top donors, and completed deliveries via live reports.</p>
        </div>
    </div>

    <div class="card mt-3">
        <h2 class="section-heading">How It Works</h2>
        <ol style="padding-left:1.5rem;line-height:2;">
            <li><strong>Donors register</strong> and list surplus food with expiry dates and pickup coordinates.</li>
            <li><strong>NGOs sign up</strong> (pending admin approval) and search available food near them.</li>
            <li><strong>NGOs request</strong> food; the donor is notified and can accept or reject.</li>
            <li>On acceptance the <strong>pickup time is scheduled</strong>; the NGO is notified immediately.</li>
            <li>After pickup the donor marks the request <strong>Completed</strong>; the NGO can leave a rating.</li>
            <li>All metrics roll up to the <strong>admin reports</strong> dashboard.</li>
        </ol>
    </div>

    <div class="flex-between mt-3">
        <% if (session.getAttribute("user") == null) { %>
        <a href="<%= ctx %>/register" class="btn btn-primary btn-lg">Get Started</a>
        <a href="<%= ctx %>/login"    class="btn btn-secondary btn-lg">Sign In</a>
        <% } else { %>
        <a href="<%= ctx %>/" class="btn btn-primary">Go to Dashboard</a>
        <% } %>
        <a href="<%= ctx %>/contact" class="btn btn-secondary">Contact Us</a>
    </div>
</div>
<footer><p>&copy; 2025 FoodShare – CS5054NT Coursework</p></footer>
<script src="<%= ctx %>/js/script.js"></script>
</body>
</html>
