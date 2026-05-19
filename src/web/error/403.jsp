<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>403 Forbidden – FoodShare</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
<%@ include file="../common/navbar.jsp" %>
<div class="page-wrapper flex-center" style="min-height:60vh;flex-direction:column;text-align:center;gap:1rem;">
    <div style="font-size:5rem;">🚫</div>
    <h1 style="font-size:3rem;color:var(--red);font-weight:800;">403</h1>
    <h2 style="color:var(--grey-800);">Access Denied</h2>
    <p class="text-muted" style="max-width:400px;">
        You don't have permission to view this page.
        Please ensure you are logged in with the correct role.
    </p>
    <div class="btn-group flex-center">
        <a href="<%= ctx %>/" class="btn btn-primary">Go to Dashboard</a>
        <a href="<%= ctx %>/login"  class="btn btn-secondary">Login</a>
    </div>
</div>
<footer><p>&copy; 2025 FoodShare</p></footer>
<script src="<%= ctx %>/js/script.js"></script>
</body>
</html>
