<%@ page contentType="text/html;charset=UTF-8" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Contact – FoodShare</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="page-wrapper" style="justify-self: center">
    <div class="page-header" style="justify-self: center">
        <h1 style="text-align: center">Contact Us</h1>
        <p style="text-align: center">Have a question or need support? Fill in the form below.</p>
    </div>

    <div style="display:grid;grid-template-columns:1fr 1fr;gap:1.5rem;max-width:860px;justify-self: center">
        <div class="form-card" style="max-width:100%;margin:0;">
            <% if ("1".equals(request.getParameter("sent"))) { %>
            <div class="alert alert-success"><span class="alert-icon">✅</span> Thank you! Your message has been received.</div>
            <% } %>
            <form action="<%= ctx %>/contact" method="get">
                <div class="form-group">
                    <label for="cname">Full Name <span class="required">*</span></label>
                    <input type="text" id="cname" name="cname" required placeholder="Jane Smith">
                </div>
                <div class="form-group">
                    <label for="cemail">Email Address <span class="required">*</span></label>
                    <input type="email" id="cemail" name="cemail" required placeholder="jane@example.com">
                </div>
                <div class="form-group">
                    <label for="subject">Subject</label>
                    <select id="subject" name="subject">
                        <option value="general">General Enquiry</option>
                        <option value="donor">Donor Support</option>
                        <option value="ngo">NGO / Shelter Support</option>
                        <option value="bug">Report a Bug</option>
                        <option value="other">Other</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="cmessage">Message <span class="required">*</span></label>
                    <textarea id="cmessage" name="cmessage" required placeholder="Write your message here…" rows="5"></textarea>
                </div>
                <button type="submit" class="btn btn-primary btn-block">Send Message</button>
            </form>
        </div>

        <div class="card" style="align-self:start;">
            <h2 class="card-title mb-2">Get in Touch</h2>
            <p class="text-small text-muted mb-2">We aim to respond within 24 hours on working days.</p>
            <ul style="display:flex;flex-direction:column;gap:.75rem;font-size:.92rem;">
                <li>📧 <a href="mailto:support@foodshare.example.com">support@foodshare.example.com</a></li>
                <li>📞 +44 (0) 20 0000 0000</li>
                <li>📍 FoodShare HQ, London, UK</li>
                <li>⏰ Mon–Fri, 09:00–17:00 GMT</li>
            </ul>
            <hr style="margin:1rem 0;border:none;border-top:1px solid var(--grey-200);">
            <p class="text-small text-muted">
                For urgent food collection issues please call our emergency line:<br>
                <strong>0800 000 0000</strong>
            </p>
        </div>
    </div>
</div>
<jsp:include page="../common/footer.jsp" />
<script src="<%= ctx %>/js/script.js"></script>
</body>
</html>
