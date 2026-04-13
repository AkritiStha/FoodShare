<%@ page contentType="text/html;charset=UTF-8" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Food Listing – FoodShare</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
<%@ include file="../common/navbar.jsp" %>
<div class="page-wrapper">
    <div class="page-header">
        <h1>🍽️ Add Food Listing</h1>
        <p>List surplus food so NGOs near you can request it.</p>
    </div>

    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-error"><span class="alert-icon">⚠️</span> <%= request.getAttribute("error") %></div>
    <% } %>

    <div class="form-card" style="max-width:680px;margin:0;">
        <form action="<%= ctx %>/donor/addFood" method="post" novalidate>

            <div class="form-group">
                <label for="name">Food Name <span class="required">*</span></label>
                <input type="text" id="name" name="name" required placeholder="e.g. Vegetable Curry" maxlength="150">
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="quantity">Quantity <span class="required">*</span></label>
                    <input type="number" id="quantity" name="quantity" required
                           placeholder="e.g. 10" min="0.1" step="0.1">
                </div>
                <div class="form-group">
                    <label for="quantityUnit">Unit <span class="required">*</span></label>
                    <select id="quantityUnit" name="quantityUnit">
                        <option value="kg">kg</option>
                        <option value="units">units</option>
                        <option value="litres">litres</option>
                        <option value="portions">portions</option>
                        <option value="boxes">boxes</option>
                    </select>
                </div>
            </div>

            <div class="form-group">
                <label for="description">Description</label>
                <textarea id="description" name="description" rows="3"
                          placeholder="Ingredients, allergens, storage notes…"></textarea>
            </div>

            <div class="form-group">
                <label for="expiryDate">Expiry Date &amp; Time <span class="required">*</span></label>
                <input type="datetime-local" id="expiryDate" name="expiryDate" required>
                <span class="form-hint">Food will automatically be hidden after this date/time.</span>
            </div>

            <div class="form-group">
                <label for="pickupLocation">Pickup Address <span class="required">*</span></label>
                <input type="text" id="pickupLocation" name="pickupLocation" required
                       placeholder="12 High Street, London, EC1A 1BB" maxlength="255">
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="latitude">Latitude</label>
                    <input type="number" id="latitude" name="latitude"
                           placeholder="e.g. 51.5074" step="0.0000001" min="-90" max="90">
                    <span class="form-hint">Optional – enables distance-based search.</span>
                </div>
                <div class="form-group">
                    <label for="longitude">Longitude</label>
                    <input type="number" id="longitude" name="longitude"
                           placeholder="e.g. -0.1278" step="0.0000001" min="-180" max="180">
                </div>
            </div>

            <button type="button" class="btn btn-secondary btn-sm mb-2" id="geoBtn">
                📍 Use My Current Location
            </button>

            <div class="btn-group mt-2">
                <button type="submit" class="btn btn-primary btn-lg">Add Listing</button>
                <a href="<%= ctx %>/donor/myListings" class="btn btn-secondary btn-lg">Cancel</a>
            </div>
        </form>
    </div>
</div>
<footer><p>&copy; 2025 FoodShare</p></footer>
<script src="<%= ctx %>/js/script.js"></script>
<script>
    document.getElementById('geoBtn').addEventListener('click', function () {
        if (!navigator.geolocation) { alert('Geolocation not supported.'); return; }
        navigator.geolocation.getCurrentPosition(function (pos) {
            document.getElementById('latitude').value  = pos.coords.latitude.toFixed(7);
            document.getElementById('longitude').value = pos.coords.longitude.toFixed(7);
        }, function () { alert('Could not retrieve location. Please enter manually.'); });
    });
</script>
</body>
</html>
