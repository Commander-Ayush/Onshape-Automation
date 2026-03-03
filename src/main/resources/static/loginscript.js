async function handleLogin() {
    const btn = document.getElementById('submitBtn');
    const spinner = document.getElementById('spinner');
    const btnText = document.getElementById('btnText');
    const msgEl = document.getElementById('waitingMessage');
    const email = document.getElementById('emailAddress').value.trim();
    const password = document.getElementById('Userpassword').value;

    // Basic client-side guard
    if (!email || !password) return;

    // ── Loading state ──────────────────────────────────────────
    btn.disabled = true;
    btn.classList.add('loading');
    btn.classList.remove('error-state');
    btnText.textContent = 'Signing in…';
    msgEl.classList.remove('error-text');
    msgEl.textContent = 'This might take 10–12 seconds, please wait.';

    try {
        const response = await fetch('http://localhost:8080/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password }),
        });

        if (response.ok) {
            // ── Success ─────────────────────────────────────────────
            btnText.textContent = '✓ Success';
            btn.classList.remove('loading');
            msgEl.textContent = '';
            // Redirect or handle success here
            // window.location.href = '/dashboard';
        } else {
            // ── Invalid credentials ──────────────────────────────────
            showError(btn, btnText, msgEl);
        }

    } catch (err) {
        // ── Network / server unreachable ─────────────────────────
        showError(btn, btnText, msgEl, 'Could not reach server. Try again.');
    }
}

function showError(btn, btnText, msgEl, customMessage) {
    btn.classList.remove('loading');
    btn.classList.add('error-state');
    btn.disabled = false;
    btnText.textContent = 'Sign up';

    msgEl.classList.add('error-text');
    msgEl.textContent = customMessage || 'Wrong credentials, please try again.';

    // Clear the error message after 2.5 seconds
    setTimeout(() => {
        msgEl.textContent = '';
        msgEl.classList.remove('error-text');
        btn.classList.remove('error-state');
    }, 2500);
}