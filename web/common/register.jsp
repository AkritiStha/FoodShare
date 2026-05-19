<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String ctx = request.getContextPath();
    java.util.Map<?,?> fd = (java.util.Map<?,?>) request.getAttribute("formData");
    java.util.function.Function<String,String> prev = key -> {
        if (fd == null) return "";
        String[] vals = (String[]) fd.get(key);
        return (vals != null && vals.length > 0) ? vals[0] : "";
    };
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register – FoodShare</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
<%@ include file="navbar.jsp" %>

<div class="auth-page" style="padding:2rem 1rem">
    <div class="auth-card" style="max-width:540px">
        <div class="auth-logo">
            <div class="auth-logo-icon">🌿</div>
            <h1>Create Account</h1>
            <p>Join FoodShare and help reduce food waste.</p>
        </div>

        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error">
            <span class="alert-icon">⚠️</span> <%= request.getAttribute("error") %>
        </div>
        <% } %>

        <form action="<%= ctx %>/register" method="post" novalidate>
            <div class="form-group">
                <label for="name">Full Name / Organisation Name <span class="required">*</span></label>
                <input type="text" id="name" name="name" required
                       placeholder="e.g. Green Leaf Restaurant"
                       value="<%= prev.apply("name") %>" minlength="2" maxlength="100">
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="email">Email Address <span class="required">*</span></label>
                    <input type="email" id="email" name="email" required
                           placeholder="you@example.com"
                           value="<%= prev.apply("email") %>" autocomplete="email">
                </div>
                <div class="form-group">
                    <label for="phone">Phone Number</label>
                    <input type="text" id="phone" name="phone"
                           placeholder="07700 123456"
                           value="<%= prev.apply("phone") %>">
                </div>
            </div>

            <div class="form-group">
                <label for="address">Address</label>
                <input type="text" id="address" name="address"
                       placeholder="Street, City, Postcode"
                       value="<%= prev.apply("address") %>">
            </div>

            <div class="form-group">
                <label for="role">I am registering as <span class="required">*</span></label>
                <select id="role" name="role" required>
                    <option value="" disabled <%= prev.apply("role").isEmpty() ? "selected" : "" %>>– Select role –</option>
                    <option value="donor" <%= "donor".equals(prev.apply("role")) ? "selected" : "" %>>
                        🍽️ Donor (Restaurant / Hotel / Individual)
                    </option>
                    <option value="ngo" <%= "ngo".equals(prev.apply("role")) ? "selected" : "" %>>
                        🤝 NGO / Shelter / Community Kitchen
                    </option>
                </select>
                <span class="form-hint">NGO accounts require admin approval before login.</span>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="password">Password <span class="required">*</span></label>
                    <input type="password" id="password" name="password" required
                           placeholder="Min 8 chars" minlength="8" autocomplete="new-password">
                    <span id="pwd-strength"></span>
                </div>
                <div class="form-group">
                    <label for="confirmPassword">Confirm Password <span class="required">*</span></label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required
                           placeholder="Repeat password" autocomplete="new-password">
                    <span id="confirm-hint" class="form-hint"></span>
                </div>
            </div>

            <button type="submit" class="btn btn-primary btn-block btn-lg" style="margin-top:.5rem">
                Create Account
            </button>
        </form>

        <div class="form-divider">already have an account?</div>
        <p class="text-center text-small">
            <a href="<%= ctx %>/login">Sign in here</a>
        </p>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
<script src="<%= ctx %>/js/script.js"></script>
</body>
</html>
