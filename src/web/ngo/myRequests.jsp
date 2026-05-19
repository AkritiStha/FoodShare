<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.Request, java.util.List" %>
<%
    List<Request> requests = (List<Request>) request.getAttribute("requests");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Requests – FoodShare</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
<%@ include file="../common/navbar.jsp" %>
<div class="page-wrapper">
    <div class="page-header flex-between">
        <div>
            <h1>📋 My Requests</h1>
            <p>Track all your food requests and pickup schedules.</p>
        </div>
        <a href="<%= ctx %>/ngo/searchFood" class="btn btn-primary">+ New Request</a>
    </div>

    <% if (request.getAttribute("success") != null) { %>
    <div class="alert alert-success"><span class="alert-icon">✅</span> <%= request.getAttribute("success") %></div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-error"><span class="alert-icon">⚠️</span> <%= request.getAttribute("error") %></div>
    <% } %>

    <% if (requests == null || requests.isEmpty()) { %>
    <div class="card">
        <div class="empty-state">
            <div class="empty-state-icon">📭</div>
            <p class="empty-state-text">No requests yet. Find food and make your first request!</p>
            <a href="<%= ctx %>/ngo/searchFood" class="btn btn-primary mt-2">Find Available Food</a>
        </div>
    </div>
    <% } else { %>
    <div style="display:flex;flex-direction:column;gap:1rem;">
        <% for (Request r : requests) { %>
        <div class="card">
            <div class="card-header">
                <div>
                    <span class="fw-bold" style="font-size:1.05rem;"><%= r.getFoodItemName() %></span>
                    <span class="text-muted text-small"> · by <strong><%= r.getDonorName() %></strong></span>
                </div>
                <span class="badge badge-<%= r.getStatus().toLowerCase() %>"><%= r.getStatus() %></span>
            </div>

            <!-- Status tracker -->
            <div class="status-steps">
                <%
                    boolean isRejected = "REJECTED".equals(r.getStatus()) || "EXPIRED".equals(r.getStatus());
                    String[] steps  = {"PENDING","ACCEPTED","COMPLETED"};
                    String[] slabels = {"Pending","Accepted","Completed"};
                    int cur = 0;
                    for (int si=0; si<steps.length; si++) {
                        if (steps[si].equals(r.getStatus())) { cur = si; break; }
                    }
                    if ("COMPLETED".equals(r.getStatus())) cur = 2;
                %>
                <% for (int si=0; si<steps.length; si++) { %>
                <div class="step <%= isRejected ? "rejected" : (si < cur ? "done" : (si == cur ? "active" : "")) %>">
                    <div class="step-dot"><%= si+1 %></div>
                    <span class="step-label"><%= slabels[si] %></span>
                </div>
                <% if (si < steps.length-1) { %>
                <div class="step-connector <%= (!isRejected && si < cur) ? "done" : "" %>"></div>
                <% } %>
                <% } %>
                <% if (isRejected) { %><span class="text-danger text-small fw-bold" style="margin-left:.75rem;">(<%= r.getStatus() %>)</span><% } %>
            </div>

            <div class="food-meta" style="margin:.5rem 0 .75rem;">
                <span class="food-meta-item">📍 <%= r.getPickupLocation() != null ? r.getPickupLocation() : "–" %></span>
                <span class="food-meta-item">📅 Requested: <%= r.getCreatedAt() != null ? r.getCreatedAt().toLocalDate() : "" %></span>
                <% if (r.getPickupTime() != null) { %>
                <span class="food-meta-item" style="color:var(--green-dark);font-weight:600;">
              🕐 Scheduled pickup: <%= r.getPickupTime().toLocalDate() %> at <%= r.getPickupTime().toLocalTime().toString().substring(0,5) %>
            </span>
                <% } %>
            </div>

            <% if (r.getMessage() != null && !r.getMessage().isEmpty()) { %>
            <p class="text-small text-muted" style="margin-bottom:.5rem;">
                💬 Your note: "<%= r.getMessage() %>"
            </p>
            <% } %>

            <!-- Rating section for completed requests -->
            <% if ("COMPLETED".equals(r.getStatus())) { %>
            <% if (r.getRating() != null) { %>
            <div style="display:flex;align-items:center;gap:.5rem;margin-top:.5rem;">
                <span class="text-small text-muted">Your rating:</span>
                <span class="stars-display" style="font-size:1.2rem;">
                <%= "★".repeat(r.getRating()) %><span style="color:var(--grey-300)"><%= "★".repeat(5 - r.getRating()) %></span>
              </span>
                <% if (r.getRatingNote() != null && !r.getRatingNote().isEmpty()) { %>
                <span class="text-small text-muted">"<%= r.getRatingNote() %>"</span>
                <% } %>
            </div>
            <% } else { %>
            <div class="btn-group" style="margin-top:.5rem;">
                <button class="btn btn-warning btn-sm" onclick="openRatingModal(<%= r.getId() %>)">
                    ⭐ Rate This Donation
                </button>
            </div>
            <% } %>
            <% } %>
        </div>
        <% } %>
    </div>
    <% } %>
</div>

<!-- Rating Modal -->
<div class="modal-overlay" id="ratingModal">
    <div class="modal-box">
        <div class="modal-header">
            <h3 class="modal-title">⭐ Rate This Donation</h3>
            <button class="modal-close" onclick="closeModal('ratingModal')">×</button>
        </div>
        <form action="<%= ctx %>/ngo/rateRequest" method="post">
            <input type="hidden" name="requestId" id="rating-request-id" value="">
            <div class="form-group">
                <label>Your Rating <span class="required">*</span></label>
                <!-- Pure CSS star rating (right-to-left trick) -->
                <div class="star-rating">
                    <input type="radio" id="s5" name="rating" value="5" required>
                    <label for="s5" title="5 stars">★</label>
                    <input type="radio" id="s4" name="rating" value="4">
                    <label for="s4" title="4 stars">★</label>
                    <input type="radio" id="s3" name="rating" value="3">
                    <label for="s3" title="3 stars">★</label>
                    <input type="radio" id="s2" name="rating" value="2">
                    <label for="s2" title="2 stars">★</label>
                    <input type="radio" id="s1" name="rating" value="1">
                    <label for="s1" title="1 star">★</label>
                </div>
            </div>
            <div class="form-group">
                <label for="ratingNote">Comments (optional)</label>
                <textarea id="ratingNote" name="ratingNote" rows="2"
                          placeholder="e.g. Food was fresh and well packaged."></textarea>
            </div>
            <div class="btn-group">
                <button type="submit" class="btn btn-primary">Submit Rating</button>
                <button type="button" class="btn btn-secondary" onclick="closeModal('ratingModal')">Cancel</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
<script src="<%= ctx %>/js/script.js"></script>
</body>
</html>
