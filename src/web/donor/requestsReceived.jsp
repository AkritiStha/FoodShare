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
    <title>Requests Received – FoodShare</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
<%@ include file="../common/navbar.jsp" %>
<div class="page-wrapper">
    <div class="page-header">
        <h1>📥 Requests Received</h1>
        <p>Manage NGO requests for your food listings.</p>
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
            <p class="empty-state-text">No requests received yet.</p>
        </div>
    </div>
    <% } else { %>
    <div style="display:flex;flex-direction:column;gap:1rem;">
        <% for (Request r : requests) { %>
        <div class="card">
            <div class="card-header">
                <div>
                    <span class="fw-bold"><%= r.getFoodItemName() %></span>
                    <span class="text-muted text-small"> · requested by <strong><%= r.getNgoName() %></strong></span>
                </div>
                <span class="badge badge-<%= r.getStatus().toLowerCase() %>"><%= r.getStatus() %></span>
            </div>

            <!-- Status progress bar -->
            <div class="status-steps">
                <%
                    String[] steps = {"PENDING","ACCEPTED","COMPLETED"};
                    String[] labels = {"Pending","Accepted","Completed"};
                    boolean rejected = "REJECTED".equals(r.getStatus()) || "EXPIRED".equals(r.getStatus());
                    int currentStep = 0;
                    for (int si = 0; si < steps.length; si++) {
                        if (steps[si].equals(r.getStatus())) { currentStep = si; break; }
                        if ("COMPLETED".equals(r.getStatus())) currentStep = 2;
                    }
                %>
                <% for (int si = 0; si < steps.length; si++) { %>
                <div class="step <%= rejected ? "rejected" : (si < currentStep ? "done" : (si == currentStep ? "active" : "")) %>">
                    <div class="step-dot"><%= si + 1 %></div>
                    <span class="step-label"><%= labels[si] %></span>
                </div>
                <% if (si < steps.length - 1) { %>
                <div class="step-connector <%= (!rejected && si < currentStep) ? "done" : "" %>"></div>
                <% } %>
                <% } %>
            </div>

            <div class="food-meta" style="margin:.5rem 0;">
                <span class="food-meta-item">📍 <%= r.getPickupLocation() %></span>
                <span class="food-meta-item">📅 Received: <%= r.getCreatedAt() != null ? r.getCreatedAt().toLocalDate() : "" %></span>
                <% if (r.getPickupTime() != null) { %>
                <span class="food-meta-item">🕐 Pickup: <%= r.getPickupTime() %></span>
                <% } %>
            </div>

            <% if (r.getMessage() != null && !r.getMessage().isEmpty()) { %>
            <p class="text-small text-muted" style="margin:.25rem 0 .75rem;">
                💬 "<%= r.getMessage() %>"
            </p>
            <% } %>

            <!-- Action buttons based on status -->
            <div class="btn-group">
                <% if ("PENDING".equals(r.getStatus())) { %>
                <button class="btn btn-primary btn-sm" onclick="openAcceptModal(<%= r.getId() %>)">✅ Accept &amp; Schedule</button>
                <form action="<%= ctx %>/donor/requests" method="post" style="display:inline"
                      data-confirm="Reject this request?">
                    <input type="hidden" name="action"    value="reject">
                    <input type="hidden" name="requestId" value="<%= r.getId() %>">
                    <button type="submit" class="btn btn-danger btn-sm">❌ Reject</button>
                </form>
                <% } else if ("ACCEPTED".equals(r.getStatus())) { %>
                <form action="<%= ctx %>/donor/requests" method="post" style="display:inline"
                      data-confirm="Mark this pickup as completed?">
                    <input type="hidden" name="action"    value="complete">
                    <input type="hidden" name="requestId" value="<%= r.getId() %>">
                    <button type="submit" class="btn btn-info btn-sm">✔️ Mark Completed</button>
                </form>
                <% } else if ("COMPLETED".equals(r.getStatus())) { %>
                <span class="text-success fw-bold text-small">✅ Pickup completed</span>
                <% if (r.getRating() != null) { %>
                <span class="stars-display" style="margin-left:.5rem;">
                <%= "★".repeat(r.getRating()) + "☆".repeat(5 - r.getRating()) %>
              </span>
                <% } %>
                <% } %>
            </div>
        </div>
        <% } %>
    </div>
    <% } %>
</div>

<!-- Accept / Schedule Modal -->
<div class="modal-overlay" id="acceptModal">
    <div class="modal-box">
        <div class="modal-header">
            <h3 class="modal-title">Accept &amp; Schedule Pickup</h3>
            <button class="modal-close" onclick="closeModal('acceptModal')">×</button>
        </div>
        <form action="<%= ctx %>/donor/requests" method="post">
            <input type="hidden" name="action"    value="accept">
            <input type="hidden" name="requestId" id="accept-request-id" value="">
            <div class="form-group">
                <label for="pickupTime">Pickup Date &amp; Time <span class="required">*</span></label>
                <input type="datetime-local" id="pickupTime" name="pickupTime" required>
            </div>
            <div class="form-group">
                <label for="scheduleNotes">Notes for NGO</label>
                <textarea id="scheduleNotes" name="scheduleNotes" placeholder="e.g. Use side entrance"></textarea>
            </div>
            <div class="btn-group">
                <button type="submit" class="btn btn-primary">Confirm</button>
                <button type="button" class="btn btn-secondary" onclick="closeModal('acceptModal')">Cancel</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
<script src="<%= ctx %>/js/script.js"></script>
</body>
</html>
