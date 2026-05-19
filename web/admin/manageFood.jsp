<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.FoodItem, java.util.List" %>
<%
  List<FoodItem> allFood = (List<FoodItem>) request.getAttribute("allFood");
  String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Manage Food – FoodShare</title>
  <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
<%@ include file="../common/navbar.jsp" %>
<div class="page-wrapper">
  <div class="page-header">
    <h1>🍽️ Manage Food Listings</h1>
    <p>View all platform food listings. Delete inappropriate or problem entries.</p>
  </div>

  <% if (request.getAttribute("success") != null) { %>
  <div class="alert alert-success"><span class="alert-icon">✅</span> <%= request.getAttribute("success") %></div>
  <% } %>
  <% if (request.getAttribute("error") != null) { %>
  <div class="alert alert-error"><span class="alert-icon">⚠️</span> <%= request.getAttribute("error") %></div>
  <% } %>

  <% long expiredCount = allFood != null ? allFood.stream().filter(f->"expired".equals(f.getStatus())).count() : 0; %>
  <% if (expiredCount > 0) { %>
  <div class="alert alert-warning">
    <span class="alert-icon">⏰</span>
    <strong><%= expiredCount %></strong> expired listing(s) automatically hidden from NGO search.
  </div>
  <% } %>

  <div class="table-wrapper">
    <table>
      <thead>
      <tr>
        <th>#</th><th>Food Name</th><th>Donor</th><th>Qty</th>
        <th>Expiry</th><th>Location</th><th>Status</th><th>Listed</th><th>Del</th>
      </tr>
      </thead>
      <tbody>
      <% if (allFood != null) { for (FoodItem f : allFood) { %>
      <tr class="<%= "expired".equals(f.getStatus()) ? "text-muted" : "" %>">
        <td class="text-small text-muted"><%= f.getId() %></td>
        <td>
          <strong><%= f.getName() %></strong>
          <% if (f.isExpiringSoon() && "available".equals(f.getStatus())) { %>
          <span class="badge badge-pending" style="font-size:.65rem;">⏰ Soon</span>
          <% } %>
        </td>
        <td class="text-small"><%= f.getDonorName() != null ? f.getDonorName() : "–" %></td>
        <td class="text-small"><%= f.getQuantity() %> <%= f.getQuantityUnit() %></td>
        <td class="text-small">
          <%= f.getExpiryDate() != null ? f.getExpiryDate().toLocalDate() : "N/A" %>
        </td>
        <td class="text-small" style="max-width:160px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">
          <%= f.getPickupLocation() %>
        </td>
        <td><span class="badge badge-<%= f.getStatus() %>"><%= f.getStatus().toUpperCase() %></span></td>
        <td class="text-small"><%= f.getCreatedAt() != null ? f.getCreatedAt().toLocalDate() : "" %></td>
        <td>
          <form action="<%= ctx %>/admin/manageFood" method="post" style="display:inline"
                data-confirm="Delete food listing '<%= f.getName() %>'?">
            <input type="hidden" name="foodId" value="<%= f.getId() %>">
            <button type="submit" class="btn btn-danger btn-sm">🗑️</button>
          </form>
        </td>
      </tr>
      <% } } %>
      </tbody>
    </table>
  </div>

  <% if (allFood == null || allFood.isEmpty()) { %>
  <div class="empty-state mt-2">
    <div class="empty-state-icon">🥡</div>
    <p class="empty-state-text">No food listings in the system yet.</p>
  </div>
  <% } %>
</div>
<jsp:include page="../common/footer.jsp" />
<script src="<%= ctx %>/js/script.js"></script>
</body>
</html>
