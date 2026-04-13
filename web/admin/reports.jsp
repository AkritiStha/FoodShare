<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.Request, java.util.List" %>
<%
    String ctx         = request.getContextPath();
    int  totalDonors   = request.getAttribute("totalDonors")    != null ? (int)    request.getAttribute("totalDonors")    : 0;
    int  totalNgos     = request.getAttribute("totalNgos")      != null ? (int)    request.getAttribute("totalNgos")      : 0;
    int  totalFood     = request.getAttribute("totalFood")      != null ? (int)    request.getAttribute("totalFood")      : 0;
    int  totalReq      = request.getAttribute("totalRequests")  != null ? (int)    request.getAttribute("totalRequests")  : 0;
    int  completed     = request.getAttribute("completedCount") != null ? (int)    request.getAttribute("completedCount") : 0;
    int  pending       = request.getAttribute("pendingCount")   != null ? (int)    request.getAttribute("pendingCount")   : 0;
    int  rejected      = request.getAttribute("rejectedCount")  != null ? (int)    request.getAttribute("rejectedCount")  : 0;
    double foodSaved   = request.getAttribute("foodSavedKg")    != null ? (double) request.getAttribute("foodSavedKg")    : 0.0;
    String topDonor    = request.getAttribute("topDonor")       != null ? (String) request.getAttribute("topDonor")       : "N/A";
    String mostReq     = request.getAttribute("mostRequested")  != null ? (String) request.getAttribute("mostRequested")  : "N/A";
    List<Request> allRequests = (List<Request>) request.getAttribute("allRequests");

    double completionRate = totalReq > 0 ? (completed * 100.0 / totalReq) : 0;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reports – FoodShare</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
<%@ include file="../common/navbar.jsp" %>
<div class="page-wrapper">
    <div class="page-header flex-between">
        <div>
            <h1>📊 Platform Reports</h1>
            <p>Aggregate metrics on donations, deliveries, and food waste reduction.</p>
        </div>
        <span class="text-muted text-small">Generated: <%= new java.util.Date() %></span>
    </div>

    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-error"><span class="alert-icon">⚠️</span> <%= request.getAttribute("error") %></div>
    <% } %>

    <!-- Platform overview -->
    <h2 class="section-heading">Platform Overview</h2>
    <div class="stats-grid">
        <div class="stat-card">
            <span class="stat-label">Donors</span>
            <span class="stat-value"><%= totalDonors %></span>
        </div>
        <div class="stat-card blue">
            <span class="stat-label">NGOs</span>
            <span class="stat-value"><%= totalNgos %></span>
        </div>
        <div class="stat-card orange">
            <span class="stat-label">Food Listings</span>
            <span class="stat-value"><%= totalFood %></span>
        </div>
        <div class="stat-card">
            <span class="stat-label">Total Requests</span>
            <span class="stat-value"><%= totalReq %></span>
        </div>
    </div>

    <!-- Delivery outcomes -->
    <h2 class="section-heading">Delivery Outcomes</h2>
    <div class="stats-grid">
        <div class="stat-card">
            <span class="stat-label">Completed</span>
            <span class="stat-value" style="color:var(--green-dark);"><%= completed %></span>
        </div>
        <div class="stat-card orange">
            <span class="stat-label">Pending</span>
            <span class="stat-value"><%= pending %></span>
        </div>
        <div class="stat-card red">
            <span class="stat-label">Rejected</span>
            <span class="stat-value"><%= rejected %></span>
        </div>
        <div class="stat-card blue">
            <span class="stat-label">Completion Rate</span>
            <span class="stat-value"><%= String.format("%.0f", completionRate) %>%</span>
        </div>
    </div>

    <!-- Environmental impact -->
    <h2 class="section-heading">Environmental Impact</h2>
    <div style="display:grid;grid-template-columns:repeat(auto-fit,minmax(220px,1fr));gap:1rem;margin-bottom:1.5rem;">
        <div class="card" style="border-left:4px solid var(--green-light);background:linear-gradient(135deg,#e8f5e9,#fff);">
            <div class="text-muted text-small" style="text-transform:uppercase;letter-spacing:.06em;">🌍 Total Food Saved</div>
            <div style="font-size:2.5rem;font-weight:800;color:var(--green-dark);line-height:1.1;margin:.4rem 0;">
                <%= String.format("%.1f", foodSaved) %><span style="font-size:1rem;font-weight:400;"> kg</span>
            </div>
            <p class="text-small text-muted">Total quantity of completed donations</p>
        </div>
        <div class="card" style="border-left:4px solid var(--amber);background:linear-gradient(135deg,#fff8e1,#fff);">
            <div class="text-muted text-small" style="text-transform:uppercase;letter-spacing:.06em;">🏆 Top Donor</div>
            <div style="font-size:1.5rem;font-weight:700;color:var(--orange);margin:.4rem 0;"><%= topDonor %></div>
            <p class="text-small text-muted">Most completed donations</p>
        </div>
        <div class="card" style="border-left:4px solid var(--blue);background:linear-gradient(135deg,var(--blue-light),#fff);">
            <div class="text-muted text-small" style="text-transform:uppercase;letter-spacing:.06em;">🍽️ Most Requested</div>
            <div style="font-size:1.5rem;font-weight:700;color:var(--blue);margin:.4rem 0;"><%= mostReq %></div>
            <p class="text-small text-muted">Most frequently requested food item</p>
        </div>
    </div>

    <!-- All Requests Table -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">📋 All Requests Log</h2>
            <span class="text-muted text-small"><%= allRequests != null ? allRequests.size() : 0 %> records</span>
        </div>
        <div class="table-wrapper">
            <table>
                <thead>
                <tr>
                    <th>#</th><th>Food Item</th><th>Donor</th><th>NGO</th>
                    <th>Status</th><th>Rating</th><th>Date</th>
                </tr>
                </thead>
                <tbody>
                <% if (allRequests != null) { for (Request r : allRequests) { %>
                <tr>
                    <td class="text-small text-muted"><%= r.getId() %></td>
                    <td><strong><%= r.getFoodItemName() %></strong></td>
                    <td class="text-small"><%= r.getDonorName() %></td>
                    <td class="text-small"><%= r.getNgoName() %></td>
                    <td><span class="badge badge-<%= r.getStatus().toLowerCase() %>"><%= r.getStatus() %></span></td>
                    <td>
                        <% if (r.getRating() != null) { %>
                        <span class="stars-display" style="font-size:.9rem;">
                  <%= "★".repeat(r.getRating()) %><span style="color:var(--grey-300)"><%= "★".repeat(5-r.getRating()) %></span>
                </span>
                        <% } else { %><span class="text-muted">–</span><% } %>
                    </td>
                    <td class="text-small"><%= r.getCreatedAt() != null ? r.getCreatedAt().toLocalDate() : "" %></td>
                </tr>
                <% } } %>
                </tbody>
            </table>
        </div>
        <% if (allRequests == null || allRequests.isEmpty()) { %>
        <div class="empty-state"><p class="empty-state-text">No requests recorded yet.</p></div>
        <% } %>
    </div>
</div>
<footer><p>&copy; 2025 FoodShare</p></footer>
<script src="<%= ctx %>/js/script.js"></script>
</body>
</html>
