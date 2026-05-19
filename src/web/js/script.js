/**
 * FoodShare – Minimal Vanilla JS
 * UX enhancements only: no framework, no external dependencies.
 */

/* ── 1. Mobile Navigation Toggle ─────────────────────────────── */
(function () {
    const toggle = document.querySelector('.nav-toggle');
    const links  = document.querySelector('.nav-links');
    if (!toggle || !links) return;

    toggle.addEventListener('click', () => {
        const open = links.classList.toggle('open');
        toggle.setAttribute('aria-expanded', String(open));
    });

    // Close menu when a link is clicked
    links.querySelectorAll('a').forEach(a => {
        a.addEventListener('click', () => links.classList.remove('open'));
    });
})();

/* ── 2. Alert Auto-Dismiss ────────────────────────────────────── */
(function () {
    document.querySelectorAll('.alert').forEach(alert => {
        const btn = document.createElement('button');
        btn.textContent = '×';
        btn.style.cssText =
            'background:none;border:none;float:right;font-size:1.3rem;' +
            'cursor:pointer;line-height:1;margin-left:.5rem;opacity:.7;';
        btn.addEventListener('click', () => alert.remove());
        alert.prepend(btn);

        // Auto-dismiss success/info alerts after 5 s
        if (alert.classList.contains('alert-success') ||
            alert.classList.contains('alert-info')) {
            setTimeout(() => {
                alert.style.transition = 'opacity .4s';
                alert.style.opacity    = '0';
                setTimeout(() => alert.remove(), 400);
            }, 5000);
        }
    });
})();

/* ── 3. Modal Helper ──────────────────────────────────────────── */
function openModal(id) {
    const modal = document.getElementById(id);
    if (modal) {
        modal.classList.add('open');
        document.body.style.overflow = 'hidden';
    }
}

function closeModal(id) {
    const modal = document.getElementById(id);
    if (modal) {
        modal.classList.remove('open');
        document.body.style.overflow = '';
    }
}

// Close modal on overlay click
document.querySelectorAll('.modal-overlay').forEach(overlay => {
    overlay.addEventListener('click', e => {
        if (e.target === overlay) closeModal(overlay.id);
    });
});

// Escape key closes any open modal
document.addEventListener('keydown', e => {
    if (e.key === 'Escape') {
        document.querySelectorAll('.modal-overlay.open').forEach(m => {
            closeModal(m.id);
        });
    }
});

/* ── 4. Password Strength Indicator ───────────────────────────── */
(function () {
    const pwdInput = document.getElementById('password');
    const indicator = document.getElementById('pwd-strength');
    if (!pwdInput || !indicator) return;

    pwdInput.addEventListener('input', () => {
        const v = pwdInput.value;
        let score = 0;
        if (v.length >= 8)             score++;
        if (/[A-Z]/.test(v))          score++;
        if (/[a-z]/.test(v))          score++;
        if (/\d/.test(v))             score++;
        if (/[^A-Za-z\d]/.test(v))    score++;

        const labels = ['', 'Weak', 'Fair', 'Good', 'Strong', 'Very Strong'];
        const colors = ['', '#c62828', '#e65100', '#f9a825', '#2e7d32', '#1b5e20'];

        indicator.textContent  = v.length ? labels[score] : '';
        indicator.style.color  = colors[score];
        indicator.style.fontWeight = '600';
        indicator.style.fontSize   = '.8rem';
    });
})();

/* ── 5. Confirm Password Match Inline Feedback ─────────────────── */
(function () {
    const pwd1 = document.getElementById('password');
    const pwd2 = document.getElementById('confirmPassword');
    const hint = document.getElementById('confirm-hint');
    if (!pwd1 || !pwd2 || !hint) return;

    function check() {
        if (!pwd2.value) { hint.textContent = ''; return; }
        if (pwd1.value === pwd2.value) {
            hint.textContent = '✓ Passwords match';
            hint.style.color = '#2e7d32';
        } else {
            hint.textContent = '✗ Passwords do not match';
            hint.style.color = '#c62828';
        }
    }
    pwd1.addEventListener('input', check);
    pwd2.addEventListener('input', check);
})();

/* ── 6. Expiry Date Minimum (prevent past dates) ──────────────── */
(function () {
    document.querySelectorAll('input[type="datetime-local"]').forEach(input => {
        if (!input.min) {
            const now = new Date();
            now.setSeconds(0, 0);
            input.min = now.toISOString().slice(0, 16);
        }
    });
})();

/* ── 7. Delete Confirmation ────────────────────────────────────── */
document.querySelectorAll('form[data-confirm]').forEach(form => {
    form.addEventListener('submit', e => {
        const msg = form.getAttribute('data-confirm') || 'Are you sure?';
        if (!confirm(msg)) e.preventDefault();
    });
});

/* ── 8. Populate request modal with dynamic food item id ──────── */
function setRequestTarget(foodItemId, foodItemName) {
    const input = document.getElementById('modal-food-id');
    const title = document.getElementById('modal-food-name');
    if (input) input.value = foodItemId;
    if (title) title.textContent = 'Request: ' + foodItemName;
    openModal('requestModal');
}

/* ── 9. Populate accept-request modal ─────────────────────────── */
function openAcceptModal(requestId) {
    const input = document.getElementById('accept-request-id');
    if (input) input.value = requestId;
    openModal('acceptModal');
}

/* ── 10. Populate rating modal ─────────────────────────────────── */
function openRatingModal(requestId) {
    const input = document.getElementById('rating-request-id');
    if (input) input.value = requestId;
    openModal('ratingModal');
}

/* ── 11. Quantity unit label update ───────────────────────────── */
(function () {
    const unitSelect = document.getElementById('quantityUnit');
    const unitLabel  = document.getElementById('qty-unit-label');
    if (!unitSelect || !unitLabel) return;
    unitSelect.addEventListener('change', () => {
        unitLabel.textContent = unitSelect.value;
    });
})();
