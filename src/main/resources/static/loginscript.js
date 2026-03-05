async function handleLogin() {
    const btn = document.getElementById('submitBtn');
    const spinner = document.getElementById('spinner');
    const btnText = document.getElementById('btnText');
    const msgEl = document.getElementById('waitingMessage');
    const emailAccount = document.getElementById('emailAddress').value.trim();
    const password = document.getElementById('Userpassword').value;

    // Basic client-side guard
    if (!emailAccount || !password) return;

    // ── Loading state ──────────────────────────────────────────
    btn.disabled = true;
    btn.classList.add('loading');
    btn.classList.remove('error-state');
    btnText.textContent = 'Signing in…';
    msgEl.classList.remove('error-text');
    msgEl.textContent = 'This might take 10–12 seconds, please wait.';

    try {
        const response = await fetch('/login-form', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ emailAccount: emailAccount, password: password }),
        });


        if (response.ok) {
            const redirectUrl = await response.text(); // "/home" or "/admin"
            btnText.textContent = 'Success!';
            btn.classList.remove('loading');
            window.location.href = redirectUrl;
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

const input1 = document.getElementById("emailAddress");
const input2 = document.getElementById("Userpassword");

input1.addEventListener("keydown", function (event) {
    if (event.key === "Enter") {
        event.preventDefault();
        input2.focus();
    }
});

input2.addEventListener("keydown", function (event) {
    if (event.key === "Enter") {
        event.preventDefault();
        handleLogin();
    }
});

const passwordInput = document.getElementById("Userpassword");
const toggleBtn = document.getElementById("togglePassword");

toggleBtn.addEventListener("click", function () {
    if (passwordInput.type === "password") {
        passwordInput.type = "text";
    } else {
        passwordInput.type = "password";
    }
});