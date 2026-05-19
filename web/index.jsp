<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.User" %>
<%
    User user = (User) session.getAttribute("user");
    String ctx = request.getContextPath();
    String role = (user != null) ? user.getRole() : "";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FoodShare — Connecting Surplus Food with Communities in Need</title>
    <meta name="description" content="FoodShare connects restaurants, hotels, and individuals with surplus food to local NGOs and shelters in real time.">

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:ital,opsz,wght@0,9..40,300;0,9..40,400;0,9..40,500;0,9..40,600;0,9..40,700&family=Source+Serif+4:ital,opsz,wght@0,8..60,300;0,8..60,400;0,8..60,600;1,8..60,400&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>

<%@ include file="common/navbar.jsp" %>


<%-- ═══ HERO ═══════════════════════════════════════════════════ --%>
<section class="fs-hero">
    <div class="fs-hero-inner">

        <div class="fs-hero-tag">
            <span class="fs-hero-tag-line"></span>
            Food waste reduction platform
        </div>

        <h1 class="fs-hero-headline">
            Connecting<br>
            surplus food with<br>
            <strong>communities in need</strong>
        </h1>

        <div class="fs-hero-rule"></div>

        <div class="fs-hero-sub-row">
            <p class="fs-hero-desc">
                FoodShare gives restaurants, hotels, and households a direct, verified channel
                to donate excess food — and gives NGOs a live, searchable feed of what's
                available nearby. No coordination overhead. No food wasted.
            </p>

            <div class="fs-hero-ctas">
                <% if (user == null) { %>
                <a href="<%= ctx %>/register" class="fs-btn-hero-primary">
                    Start donating
                    <svg width="14" height="14" viewBox="0 0 14 14" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                        <path d="M2.5 7H11.5M7 2.5L11.5 7L7 11.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                </a>
                <a href="<%= ctx %>/ngo/searchFood" class="fs-btn-hero-outline">Browse food</a>
                <% } else { %>
                <a href="<%= ctx %>/<%= role %>/dashboard" class="fs-btn-hero-primary">
                    Go to dashboard
                    <svg width="14" height="14" viewBox="0 0 14 14" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                        <path d="M2.5 7H11.5M7 2.5L11.5 7L7 11.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                </a>
                <% } %>
            </div>
        </div>

    </div>

    <%-- Metrics strip --%>
    <div class="fs-hero-metrics" aria-hidden="true">
        <div class="fs-hero-metric">
            <div class="fs-hero-metric-num" style="text-align: center;"
            >1,200+</div>
            <div class="fs-hero-metric-lbl" style="text-align: center;" >Donations posted</div>
        </div>
        <div class="fs-hero-metric">
            <div class="fs-hero-metric-num" style="text-align: center;" >84</div>
            <div class="fs-hero-metric-lbl" style="text-align: center;" >Active NGO partners</div>
        </div>
        <div class="fs-hero-metric">
            <div class="fs-hero-metric-num" style="text-align: center;" >4.1 t</div>
            <div class="fs-hero-metric-lbl" style="text-align: center;" >Food diverted from landfill</div>
        </div>
        <div class="fs-hero-metric">
            <div class="fs-hero-metric-num" style="text-align: center;" >12k</div>
            <div class="fs-hero-metric-lbl" style="text-align: center;" >Meals redistributed</div>
        </div>
    </div>
</section>


<%-- ═══ TRUST BAR ═══════════════════════════════════════════════ --%>
<div class="fs-trust-bar">
    <div class="fs-trust-bar-inner">
        <span class="fs-trust-bar-label">Trusted by organisations including</span>
        <div class="fs-trust-bar-items">
            <span class="fs-trust-bar-item">City Food Bank</span>
            <span class="fs-trust-bar-item">Sunrise Shelter Network</span>
            <span class="fs-trust-bar-item">Metro Community Kitchen</span>
            <span class="fs-trust-bar-item">Hope in a Bowl</span>
            <span class="fs-trust-bar-item">Urban Harvest Initiative</span>
        </div>
    </div>
</div>


<%-- ═══ FEATURES ═══════════════════════════════════════════════ --%>
<section class="fs-section">
    <div class="fs-section-inner">
        <div class="fs-section-head-row">
            <div class="fs-section-head-left">
                <div class="fs-eyebrow">Platform capabilities</div>
                <h2 class="fs-section-h2">Built for the entire food<br>recovery chain</h2>
                <p class="fs-section-body" style="margin-top:.75rem;">
                    From a restaurant posting a surplus listing to an NGO confirming collection,
                    every step is handled in one place.
                </p>
            </div>
            <% if (user == null) { %>
            <a href="<%= ctx %>/register" class="fs-btn-secondary" style="white-space:nowrap;">Create an account</a>
            <% } %>
        </div>

        <div class="fs-features-grid">
            <div class="fs-feature-card">
                <div class="fs-feature-icon">
                    <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/></svg>
                </div>
                <h3>Fast food listing</h3>
                <p>Post surplus food in under two minutes — quantity, expiry time, pickup address, and dietary notes included.</p>
            </div>
            <div class="fs-feature-card">
                <div class="fs-feature-icon">
                    <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
                </div>
                <h3>NGO discovery</h3>
                <p>Verified NGOs search and filter available food by distance, category, and quantity. No middlemen required.</p>
            </div>
            <div class="fs-feature-card">
                <div class="fs-feature-icon">
                    <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><polyline points="20 6 9 17 4 12"/></svg>
                </div>
                <h3>Verified organisations</h3>
                <p>Every NGO undergoes admin approval before requesting food, ensuring safe and accountable distribution.</p>
            </div>
            <div class="fs-feature-card">
                <div class="fs-feature-icon">
                    <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><rect x="3" y="3" width="18" height="18"/><path d="M3 9h18M9 21V9"/></svg>
                </div>
                <h3>Request management</h3>
                <p>Donors review, approve, and coordinate pickup requests through a clean role-based dashboard.</p>
            </div>
        </div>
    </div>
</section>


<%-- ═══ HOW IT WORKS ═══════════════════════════════════════════ --%>
<section class="fs-section alt">
    <div class="fs-section-inner">
        <div style="margin-bottom:3.5rem;">
            <div class="fs-eyebrow">How it works</div>
            <h2 class="fs-section-h2">Three steps from surplus to served</h2>
        </div>
        <div class="fs-steps-grid">
            <div class="fs-step">
                <div class="fs-step-num">01</div>
                <h3>Post a donation</h3>
                <p>Donors create a listing with food details, available quantity, and a collection window. It goes live immediately.</p>
            </div>
            <div class="fs-step">
                <div class="fs-step-num">02</div>
                <h3>NGO submits a request</h3>
                <p>Verified NGOs browse live listings, filter by proximity and food type, then send a collection request to the donor.</p>
            </div>
            <div class="fs-step">
                <div class="fs-step-num">03</div>
                <h3>Confirm and collect</h3>
                <p>The donor approves the request. The NGO coordinates pickup. Both parties track the status through their dashboards.</p>
            </div>
        </div>
        <div style="margin-top:3rem;text-align:center;">
            <a href="<%= ctx %>/about" class="fs-btn-secondary">Read about our mission</a>
        </div>
    </div>
</section>


<%-- ═══ IMPACT METRICS ════════════════════════════════════════ --%>
<section class="fs-section dark">
    <div class="fs-section-inner">
        <div style="margin-bottom:3rem;text-align:center;">
            <div class="fs-eyebrow light">Measurable impact</div>
            <h2 class="fs-section-h2 light">The numbers behind the mission</h2>
            <p class="fs-section-body light" style="margin:0 auto;text-align:center;">
                Every listing posted and every request approved contributes to a tangible, trackable reduction in food waste.
            </p>
        </div>
        <div class="fs-metrics-grid">
            <div class="fs-metric"><div class="fs-metric-num">1,200+</div><div class="fs-metric-lbl">Food donations posted</div></div>
            <div class="fs-metric"><div class="fs-metric-num">84</div><div class="fs-metric-lbl">Active NGO partners</div></div>
            <div class="fs-metric"><div class="fs-metric-num">4.1 t</div><div class="fs-metric-lbl">Food diverted from landfill</div></div>
            <div class="fs-metric"><div class="fs-metric-num">12k</div><div class="fs-metric-lbl">Meals redistributed</div></div>
        </div>
    </div>
</section>


<%-- ═══ SPLIT — FOR DONORS ════════════════════════════════════ --%>
<section class="fs-section">
    <div class="fs-section-inner">
        <div class="fs-split">
            <div class="fs-split-visual" aria-hidden="true">
                <div style="font-size:.72rem;font-weight:700;letter-spacing:.09em;text-transform:uppercase;color:var(--fs-soft);margin-bottom:.25rem;">Live listings</div>
                <div class="fs-list-item"><div class="fs-list-dot green"></div><div class="fs-list-info"><div class="fs-list-title">Sourdough Bread &times; 30 units</div><div class="fs-list-meta">Pickup by 7:00 PM &middot; Artisan Quarter</div></div><span class="fs-list-badge green">Available</span></div>
                <div class="fs-list-item"><div class="fs-list-dot amber"></div><div class="fs-list-info"><div class="fs-list-title">Vegetable curry &times; 45 portions</div><div class="fs-list-meta">Pickup by 2:00 PM &middot; Riverside Kitchen</div></div><span class="fs-list-badge amber">Requested</span></div>
                <div class="fs-list-item"><div class="fs-list-dot green"></div><div class="fs-list-info"><div class="fs-list-title">Seasonal produce box</div><div class="fs-list-meta">Pickup by 5:00 PM &middot; Northside Farm</div></div><span class="fs-list-badge green">Available</span></div>
                <div class="fs-list-item"><div class="fs-list-dot green"></div><div class="fs-list-info"><div class="fs-list-title">Pastry assortment &times; 20 boxes</div><div class="fs-list-meta">Pickup by 4:30 PM &middot; Central Patisserie</div></div><span class="fs-list-badge green">Available</span></div>
            </div>
            <div class="fs-split-content">
                <div class="fs-eyebrow">For food donors</div>
                <h2 class="fs-section-h2">Turn surplus into<br>community support</h2>
                <p class="fs-section-body" style="margin-top:.75rem;">Whether you run a restaurant, hotel, or catering business — FoodShare gives you a direct, verified channel to distribute surplus food responsibly.</p>
                <div class="fs-checklist">
                    <div class="fs-check-item"><div class="fs-check-icon"><svg viewBox="0 0 12 12" fill="none"><path d="M2 6.5L4.5 9L10 3" stroke="#1e4d2b" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg></div>Post listings in under two minutes</div>
                    <div class="fs-check-item"><div class="fs-check-icon"><svg viewBox="0 0 12 12" fill="none"><path d="M2 6.5L4.5 9L10 3" stroke="#1e4d2b" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg></div>Approve or decline NGO requests on your terms</div>
                    <div class="fs-check-item"><div class="fs-check-icon"><svg viewBox="0 0 12 12" fill="none"><path d="M2 6.5L4.5 9L10 3" stroke="#1e4d2b" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg></div>Track every donation and see verified recipients</div>
                    <div class="fs-check-item"><div class="fs-check-icon"><svg viewBox="0 0 12 12" fill="none"><path d="M2 6.5L4.5 9L10 3" stroke="#1e4d2b" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg></div>View your full donation history in the dashboard</div>
                </div>
                <div class="fs-split-cta"><a href="<%= ctx %>/register" class="fs-btn-primary">Register as a donor</a></div>
            </div>
        </div>
    </div>
</section>


<%-- ═══ SPLIT — FOR NGOs ══════════════════════════════════════ --%>
<section class="fs-section alt">
    <div class="fs-section-inner">
        <div class="fs-split reverse">
            <div class="fs-split-content">
                <div class="fs-eyebrow">For NGOs and shelters</div>
                <h2 class="fs-section-h2">Find the food your<br>community needs</h2>
                <p class="fs-section-body" style="margin-top:.75rem;">Stop relying on cold calls and manual coordination. Browse live food listings near you and send requests directly to donors.</p>
                <div class="fs-checklist">
                    <div class="fs-check-item"><div class="fs-check-icon"><svg viewBox="0 0 12 12" fill="none"><path d="M2 6.5L4.5 9L10 3" stroke="#1e4d2b" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg></div>Apply once for NGO verification — access all listings</div>
                    <div class="fs-check-item"><div class="fs-check-icon"><svg viewBox="0 0 12 12" fill="none"><path d="M2 6.5L4.5 9L10 3" stroke="#1e4d2b" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg></div>Search by distance, food type, and availability</div>
                    <div class="fs-check-item"><div class="fs-check-icon"><svg viewBox="0 0 12 12" fill="none"><path d="M2 6.5L4.5 9L10 3" stroke="#1e4d2b" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg></div>Track request statuses in one central dashboard</div>
                    <div class="fs-check-item"><div class="fs-check-icon"><svg viewBox="0 0 12 12" fill="none"><path d="M2 6.5L4.5 9L10 3" stroke="#1e4d2b" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg></div>Rate donations to build a trusted donor network</div>
                </div>
                <div class="fs-split-cta"><a href="<%= ctx %>/register?role=ngo" class="fs-btn-secondary">Apply as an NGO</a></div>
            </div>
            <div class="fs-split-visual" aria-hidden="true" style="background:#f4f8f4;">
                <div style="font-size:.72rem;font-weight:700;letter-spacing:.09em;text-transform:uppercase;color:var(--fs-soft);margin-bottom:.25rem;">Recent requests</div>
                <div class="fs-list-item"><div class="fs-list-dot green"></div><div class="fs-list-info"><div class="fs-list-title">Request #1042 — Approved</div><div class="fs-list-meta">Harvest Bakehouse &middot; 45 portions collected</div></div></div>
                <div class="fs-list-item"><div class="fs-list-dot amber"></div><div class="fs-list-info"><div class="fs-list-title">Request #1038 — Pending</div><div class="fs-list-meta">Grand Meridian Hotel &middot; Awaiting donor</div></div></div>
                <div class="fs-list-item"><div class="fs-list-dot blue"></div><div class="fs-list-info"><div class="fs-list-title">Request #1031 — Completed</div><div class="fs-list-meta">Community Kitchen &middot; Rated 5 stars</div></div></div>
                <div style="margin-top:1rem;padding:1rem;background:#fff;border:1px solid #e0e0e0;border-radius:4px;">
                    <div style="font-size:.78rem;color:var(--fs-mid);margin-bottom:.5rem;font-weight:600;">This week</div>
                    <div style="display:flex;gap:1.75rem;">
                        <div><div style="font-size:1.35rem;font-weight:700;color:var(--fs-forest);letter-spacing:-.03em;">3</div><div style="font-size:.72rem;color:var(--fs-soft);">Requests</div></div>
                        <div><div style="font-size:1.35rem;font-weight:700;color:var(--fs-forest);letter-spacing:-.03em;">2</div><div style="font-size:.72rem;color:var(--fs-soft);">Approved</div></div>
                        <div><div style="font-size:1.35rem;font-weight:700;color:var(--fs-forest);letter-spacing:-.03em;">90</div><div style="font-size:.72rem;color:var(--fs-soft);">Portions</div></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>


<%-- ═══ CTA BANNER (logged out only) ════════════════════════════ --%>
<% if (user == null) { %>
<section class="fs-cta-banner">
    <div class="fs-cta-inner">
        <div class="fs-cta-text">
            <h2>Ready to make a difference?</h2>
            <p>Join FoodShare today — free for donors and verified NGOs.</p>
        </div>
        <div class="fs-cta-actions">
            <a href="<%= ctx %>/register" class="fs-btn-cta-primary">Create an account</a>
            <a href="<%= ctx %>/about" class="fs-btn-cta-outline">Learn more</a>
        </div>
    </div>
</section>
<% } %>


<%@ include file="common/footer.jsp" %>
<script src="<%= ctx %>/js/script.js"></script>
</body>
</html>
