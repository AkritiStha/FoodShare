<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.FoodItem, java.util.List" %>
<%
    List<FoodItem> foodItems = (List<FoodItem>) request.getAttribute("foodItems");
    String keyword  = request.getAttribute("keyword")  != null ? (String) request.getAttribute("keyword")  : "";
    String latParam = request.getAttribute("lat")       != null ? (String) request.getAttribute("lat")      : "";
    String lonParam = request.getAttribute("lon")       != null ? (String) request.getAttribute("lon")      : "";
    String ctx      = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Find Food – FoodShare</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
<%@ include file="../common/navbar.jsp" %>
<div class="page-wrapper">
    <div class="page-header">
        <h1>🔍 Find Available Food</h1>
        <p>Search food near you. Results are sorted by distance when you share your location.</p>
    </div>

    <% if (request.getAttribute("success") != null) { %>
    <div class="alert alert-success"><span class="alert-icon">✅</span> <%= request.getAttribute("success") %></div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-error"><span class="alert-icon">⚠️</span> <%= request.getAttribute("error") %></div>
    <% } %>

    <!-- Search Form -->
    <form action="<%= ctx %>/ngo/searchFood" method="get" class="card mb-2">
        <div class="search-bar" style="margin-bottom:0;">
            <div class="form-group" style="flex:2 1 240px;margin-bottom:0;">
                <label for="keyword">Search by food name or description</label>
                <input type="text" id="keyword" name="keyword"
                       placeholder="e.g. rice, curry, bread…"
                       value="<%= keyword %>">
            </div>
            <div class="form-group" style="flex:1 1 140px;margin-bottom:0;">
                <label for="lat">Your Latitude</label>
                <input type="number" id="lat" name="lat" placeholder="51.5074"
                       step="0.0000001" value="<%= latParam %>">
            </div>
            <div class="form-group" style="flex:1 1 140px;margin-bottom:0;">
                <label for="lon">Your Longitude</label>
                <input type="number" id="lon" name="lon" placeholder="-0.1278"
                       step="0.0000001" value="<%= lonParam %>">
            </div>
            <div style="display:flex;flex-direction:column;gap:.5rem;align-self:flex-end;">
                <button type="button" class="btn btn-secondary btn-sm" id="geoBtn">📍 My Location</button>
                <button type="submit" class="btn btn-primary">Search</button>
            </div>
        </div>
    </form>

    <!-- Results -->
    <% if (foodItems == null) { %>
    <div class="card">
        <div class="empty-state">
            <div class="empty-state-icon">🔍</div>
            <p class="empty-state-text">Use the search above to find available food near you.</p>
        </div>
    </div>
    <% } else if (foodItems.isEmpty()) { %>
    <div class="card">
        <div class="empty-state">
            <div class="empty-state-icon">😕</div>
            <p class="empty-state-text">No available food found matching your search.</p>
            <a href="<%= ctx %>/ngo/searchFood" class="btn btn-secondary mt-2">Clear Search</a>
        </div>
    </div>
    <% } else { %>
    <p class="text-muted text-small mb-2">
        Found <strong><%= foodItems.size() %></strong> item(s)
        <% if (!latParam.isEmpty()) { %> · sorted by distance from your location<% } %>
    </p>
    <div class="food-grid">
        <% for (FoodItem item : foodItems) { %>
        <div class="food-card">
            <div class="food-card-header">
                <span class="food-card-title"><%= item.getName() %></span>
                <span class="badge badge-available">Available</span>
            </div>
            <div class="food-card-body">
                <div class="food-meta">
                    <span class="food-meta-item">⚖️ <%= item.getQuantity() %> <%= item.getQuantityUnit() %></span>
                    <span class="food-meta-item">👤 <%= item.getDonorName() != null ? item.getDonorName() : "Donor" %></span>
                    <span class="food-meta-item">📍 <%= item.getPickupLocation() %></span>
                    <span class="food-meta-item">
                🕐 Expires: <strong><%= item.getExpiryDate() != null ? item.getExpiryDate().toLocalDate() : "N/A" %></strong>
                at <%= item.getExpiryDate() != null ? item.getExpiryDate().toLocalTime().toString().substring(0, 5) : "" %>
              </span>
                </div>

                <% if (item.isExpiringSoon()) { %>
                <div class="expiry-warning">⏰ Expiring within 24 hours – act fast!</div>
                <% } %>

                <% if (item.getDescription() != null && !item.getDescription().isEmpty()) { %>
                <p class="text-small text-muted" style="margin-top:.35rem;"><%= item.getDescription() %></p>
                <% } %>

                <% if (item.getDistanceKm() > 0) { %>
                <div class="distance-badge">📏 <%= String.format("%.1f", item.getDistanceKm()) %> km away</div>
                <% } %>
            </div>
            <div class="food-card-footer">
                <button class="btn btn-primary btn-sm"
                        onclick="setRequestTarget(<%= item.getId() %>, '<%= item.getName().replace("'","\'") %>')">
                    🤝 Request
                </button>
                <span class="text-muted text-small">ID #<%= item.getId() %></span>
            </div>
        </div>
        <% } %>
    </div>
    <% } %>
</div>

<!-- Request Modal -->
<div class="modal-overlay" id="requestModal">
    <div class="modal-box">
        <div class="modal-header">
            <h3 class="modal-title" id="modal-food-name">Request Food</h3>
            <button class="modal-close" onclick="closeModal('requestModal')">×</button>
        </div>
        <form action="<%= ctx %>/ngo/requestFood" method="post">
            <input type="hidden" name="foodItemId" id="modal-food-id" value="">
            <div class="form-group">
                <label for="message">Message to Donor (optional)</label>
                <textarea id="message" name="message" rows="3"
                          placeholder="e.g. We can collect by 4 PM today. We serve 80 people."></textarea>
            </div>
            <div class="alert alert-info" style="font-size:.85rem;">
                <span class="alert-icon">ℹ️</span>
                Once you submit this request, the food item will be reserved pending donor approval.
            </div>
            <div class="btn-group">
                <button type="submit" class="btn btn-primary">Submit Request</button>
                <button type="button" class="btn btn-secondary" onclick="closeModal('requestModal')">Cancel</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
<script src="<%= ctx %>/js/script.js"></script>
<script>
    document.getElementById('geoBtn').addEventListener('click', function () {
        if (!navigator.geolocation) { alert('Geolocation not supported.'); return; }
        navigator.geolocation.getCurrentPosition(function (pos) {
            document.getElementById('lat').value = pos.coords.latitude.toFixed(7);
            document.getElementById('lon').value = pos.coords.longitude.toFixed(7);
        }, function () { alert('Could not get location. Please enter manually.'); });
    });
</script>
</body>
</html>
