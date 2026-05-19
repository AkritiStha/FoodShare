<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.FoodItem, java.util.List" %>
<%
    List<FoodItem> listings = (List<FoodItem>) request.getAttribute("listings");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Listings – FoodShare</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
<%@ include file="../common/navbar.jsp" %>
<div class="page-wrapper">
    <div class="page-header flex-between">
        <div>
            <h1>📋 My Food Listings</h1>
            <p>All food items you have listed for donation.</p>
        </div>
        <a href="<%= ctx %>/donor/addFood" class="btn btn-primary">+ Add New</a>
    </div>

    <% if (request.getAttribute("success") != null) { %>
    <div class="alert alert-success"><span class="alert-icon">✅</span> <%= request.getAttribute("success") %></div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-error"><span class="alert-icon">⚠️</span> <%= request.getAttribute("error") %></div>
    <% } %>

    <% if (listings == null || listings.isEmpty()) { %>
    <div class="card">
        <div class="empty-state">
            <div class="empty-state-icon">🥡</div>
            <p class="empty-state-text">You haven't listed any food yet.</p>
            <a href="<%= ctx %>/donor/addFood" class="btn btn-primary mt-2">Add Your First Listing</a>
        </div>
    </div>
    <% } else { %>
    <div class="food-grid">
        <% for (FoodItem item : listings) { %>
        <div class="food-card">
            <div class="food-card-header">
                <span class="food-card-title"><%= item.getName() %></span>
                <span class="badge badge-<%= item.getStatus() %>"><%= item.getStatus().toUpperCase() %></span>
            </div>
            <div class="food-card-body">
                <div class="food-meta">
                    <span class="food-meta-item">⚖️ <%= item.getQuantity() %> <%= item.getQuantityUnit() %></span>
                    <span class="food-meta-item">📍 <%= item.getPickupLocation() %></span>
                    <span class="food-meta-item">
                🕐 Expires: <%= item.getExpiryDate() != null ? item.getExpiryDate().toLocalDate() : "N/A" %>
              </span>
                </div>
                <% if (item.isExpiringSoon() && "available".equals(item.getStatus())) { %>
                <div class="expiry-warning">⏰ Expires within 24 hours!</div>
                <% } %>
                <% if (item.getDescription() != null && !item.getDescription().isEmpty()) { %>
                <p class="text-small text-muted"><%= item.getDescription() %></p>
                <% } %>
            </div>
            <div class="food-card-footer">
                <% if ("available".equals(item.getStatus()) || "expired".equals(item.getStatus())) { %>
                <a href="<%= ctx %>/donor/updateFood?id=<%= item.getId() %>"
                   class="btn btn-secondary btn-sm">✏️ Edit</a>
                <form action="<%= ctx %>/donor/deleteFood" method="post" style="display:inline"
                      data-confirm="Delete this food listing permanently?">
                    <input type="hidden" name="id" value="<%= item.getId() %>">
                    <button type="submit" class="btn btn-danger btn-sm">🗑️ Delete</button>
                </form>
                <% } else { %>
                <span class="text-muted text-small">
                <%= "requested".equals(item.getStatus()) ? "⏳ Awaiting pickup" : "✅ Donated" %>
              </span>
                <% } %>
                <span class="text-muted text-small">Listed <%= item.getCreatedAt() != null ? item.getCreatedAt().toLocalDate() : "" %></span>
            </div>
        </div>
        <% } %>
    </div>
    <% } %>
</div>
<jsp:include page="../common/footer.jsp" />
<script src="<%= ctx %>/js/script.js"></script>
</body>
</html>
