<%-- FoodShare – Redesigned Footer Component --%>
<% String footerCtx = request.getContextPath(); %>
<footer class="fs-footer">
    <div class="fs-footer-inner">
        <div class="fs-footer-top">

            <%-- Brand --%>
            <div class="fs-footer-brand">
                <div class="fs-logo-mark" style="margin-bottom:1rem;">
                    <svg viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg" style="fill:white;width:18px;height:18px;">
                        <path d="M10 2 C7 5 5 8 6.5 11.5 C7.5 14 12.5 14 13.5 11.5 C15 8 13 5 10 2Z"/>
                        <path d="M10 14 L10 18" style="stroke:white;stroke-width:1.5;stroke-linecap:round;fill:none;"/>
                    </svg>
                </div>
                <span class="fs-footer-brand-name">FoodShare</span>
                <p>Reducing food waste and fighting hunger by connecting surplus food donors with organizations and communities in need.</p>
            </div>

            <%-- Platform --%>
            <div class="fs-footer-col">
                <h4>Platform</h4>
                <ul class="fs-footer-nav">
                    <li><a href="<%= footerCtx %>/">Home</a></li>
                    <li><a href="<%= footerCtx %>/donor/addFood">Donate Food</a></li>
                    <li><a href="<%= footerCtx %>/ngo/searchFood">Browse Listings</a></li>
                    <li><a href="<%= footerCtx %>/register">Register</a></li>
                    <li><a href="<%= footerCtx %>/login">Log In</a></li>
                </ul>
            </div>

            <%-- Organisation --%>
            <div class="fs-footer-col">
                <h4>Organisation</h4>
                <ul class="fs-footer-nav">
                    <li><a href="<%= footerCtx %>/about">About Us</a></li>
                    <li><a href="<%= footerCtx %>/contact">Contact</a></li>
                    <li><a href="#">Privacy Policy</a></li>
                    <li><a href="#">Terms of Use</a></li>
                </ul>
            </div>

            <%-- Contact --%>
            <div class="fs-footer-col">
                <h4>Contact</h4>

                <div class="fs-footer-contact-item">
                    <svg class="fs-footer-contact-icon" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg"
                         style="stroke:rgba(255,255,255,0.55);stroke-width:1.5;stroke-linecap:round;stroke-linejoin:round; width:22px;
height:22px;
min-width:22px;
min-height:22px;
display:block;">
                        <path d="M2.5 6.667 10 11.25l7.5-4.583"/>
                        <rect x="2.5" y="4.167" width="15" height="11.667" rx="1.5"/>
                    </svg>
                    <a href="mailto:info@foodshare.com" style="color:rgba(255,255,255,0.55);text-decoration:none;">
                        info@foodshare.com
                    </a>
                </div>

                <div class="fs-footer-contact-item">
                    <svg class="fs-footer-contact-icon" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg"
                         style="stroke:rgba(255,255,255,0.55);stroke-width:1.5;stroke-linecap:round;stroke-linejoin:round; width:22px;
height:22px;
min-width:22px;
min-height:22px;
display:block;">
                        <path d="M3.333 4.167 C3.333 3.706 3.706 3.333 4.167 3.333 H6.25 C6.481 3.333 6.681 3.481 6.75 3.7 L7.583 6.2 C7.659 6.44 7.571 6.703 7.363 6.845 L5.863 7.862 C6.905 10.059 8.774 11.928 10.971 12.97 L11.988 11.47 C12.13 11.262 12.393 11.174 12.633 11.25 L15.133 12.083 C15.352 12.152 15.5 12.352 15.5 12.583 V14.667 C15.5 15.127 15.127 15.5 14.667 15.5 H13.75 C8.003 15.5 3.333 10.83 3.333 5.083 V4.167Z"/>
                    </svg>
                    <a href="tel:+1234567890" style="color:rgba(255,255,255,0.55);text-decoration:none;">
                        +1 234 567 890
                    </a>
                </div>

                <div class="fs-footer-contact-item">
                    <svg class="fs-footer-contact-icon" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg"
                         style="stroke:rgba(255,255,255,0.55);stroke-width:1.5;stroke-linecap:round;stroke-linejoin:round; width:22px;
height:22px;
min-width:22px;
min-height:22px;
display:block;">
                        <path d="M10 1.667A5.833 5.833 0 0 0 4.167 7.5C4.167 11.875 10 18.333 10 18.333S15.833 11.875 15.833 7.5A5.833 5.833 0 0 0 10 1.667Z"/>
                        <circle cx="10" cy="7.5" r="2.083"/>
                    </svg>
                    123 Community Lane, City
                </div>
            </div>

        </div>

        <%-- Bottom bar --%>
        <div class="fs-footer-bottom">
            <p class="fs-footer-copy">
                &copy; <%= java.time.Year.now().getValue() %> FoodShare. All rights reserved.
            </p>
            <div class="fs-footer-socials">
                <%-- Twitter/X --%>
                <a href="#" class="fs-social-link" aria-label="Twitter">
                    <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                        <path d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z"
                              style="fill:rgba(255,255,255,0.55)"/>
                    </svg>
                </a>
                <%-- LinkedIn --%>
                <a href="#" class="fs-social-link" aria-label="LinkedIn">
                    <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                        <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433a2.062 2.062 0 0 1-2.063-2.065 2.064 2.064 0 1 1 2.063 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"
                              style="fill:rgba(255,255,255,0.55)"/>
                    </svg>
                </a>
                <%-- GitHub --%>
                <a href="#" class="fs-social-link" aria-label="GitHub">
                    <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                        <path d="M12 .297c-6.63 0-12 5.373-12 12 0 5.303 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61C4.422 18.07 3.633 17.7 3.633 17.7c-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.606-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 22.092 24 17.592 24 12.297c0-6.627-5.373-12-12-12"
                              style="fill:rgba(255,255,255,0.55)"/>
                    </svg>
                </a>
            </div>
        </div>
    </div>
</footer>
