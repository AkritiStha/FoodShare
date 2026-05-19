<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.FoodItem, java.time.format.DateTimeFormatter" %>
<%
  FoodItem item = (FoodItem) request.getAttribute("foodItem");
  String ctx    = request.getContextPath();
  DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
  String expiryVal = item.getExpiryDate() != null ? item.getExpiryDate().format(dtFmt) : "";
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Edit Listing – FoodShare</title>
  <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
<%@ include file="../common/navbar.jsp" %>
<div class="page-wrapper">
  <div class="page-header">
    <h1 style="text-align: center">✏️ Edit Food Listing</h1>
    <p style="text-align: center">Update the details for: <strong><%= item.getName() %></strong></p>
  </div>

  <% if (request.getAttribute("error") != null) { %>
  <div class="alert alert-error"><span class="alert-icon">⚠️</span> <%= request.getAttribute("error") %></div>
  <% } %>

  <div class="form-card" style="max-width:680px;margin:0; justify-self: center">
    <form action="<%= ctx %>/donor/updateFood" method="post" novalidate>
      <input type="hidden" name="id" value="<%= item.getId() %>">

      <div class="form-group">
        <label for="name">Food Name <span class="required">*</span></label>
        <input type="text" id="name" name="name" required value="<%= item.getName() %>" maxlength="150">
      </div>

      <div class="form-row">
        <div class="form-group">
          <label for="quantity">Quantity <span class="required">*</span></label>
          <input type="number" id="quantity" name="quantity" required
                 value="<%= item.getQuantity() %>" min="0.1" step="0.1">
        </div>
        <div class="form-group">
          <label for="quantityUnit">Unit</label>
          <select id="quantityUnit" name="quantityUnit">
            <option value="kg"       <%= "kg".equals(item.getQuantityUnit())       ? "selected" : "" %>>kg</option>
            <option value="units"    <%= "units".equals(item.getQuantityUnit())    ? "selected" : "" %>>units</option>
            <option value="litres"   <%= "litres".equals(item.getQuantityUnit())   ? "selected" : "" %>>litres</option>
            <option value="portions" <%= "portions".equals(item.getQuantityUnit()) ? "selected" : "" %>>portions</option>
            <option value="boxes"    <%= "boxes".equals(item.getQuantityUnit())    ? "selected" : "" %>>boxes</option>
          </select>
        </div>
      </div>

      <div class="form-group">
        <label for="description">Description</label>
        <textarea id="description" name="description" rows="3"><%= item.getDescription() != null ? item.getDescription() : "" %></textarea>
      </div>

      <div class="form-group">
        <label for="expiryDate">Expiry Date &amp; Time <span class="required">*</span></label>
        <input type="datetime-local" id="expiryDate" name="expiryDate" required value="<%= expiryVal %>">
      </div>

      <div class="form-group">
        <label for="pickupLocation">Pickup Address <span class="required">*</span></label>
        <input type="text" id="pickupLocation" name="pickupLocation" required
               value="<%= item.getPickupLocation() %>" maxlength="255">
      </div>

      <div class="form-row">
        <div class="form-group">
          <label for="latitude">Latitude</label>
          <input type="number" id="latitude" name="latitude"
                 value="<%= item.getLatitude() %>" step="0.0000001" min="-90" max="90">
        </div>
        <div class="form-group">
          <label for="longitude">Longitude</label>
          <input type="number" id="longitude" name="longitude"
                 value="<%= item.getLongitude() %>" step="0.0000001" min="-180" max="180">
        </div>
      </div>

      <div class="btn-group mt-2">
        <button type="submit" class="btn btn-primary btn-lg">Save Changes</button>
        <a href="<%= ctx %>/donor/myListings" class="btn btn-secondary btn-lg">Cancel</a>
      </div>
    </form>
  </div>
</div>
<jsp:include page="../common/footer.jsp" />
<script src="<%= ctx %>/js/script.js"></script>
</body>
</html>
