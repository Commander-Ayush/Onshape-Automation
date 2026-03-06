async function uploadAssignment() {
    const btn     = document.getElementById('submitBtn');
    const btnText = document.getElementById('btnText');
    const msgEl   = document.getElementById('waitingMessage');

    const name           = document.getElementById('name').value.trim();
    const dimension      = document.getElementById('dimension').value.trim();
    const college        = document.getElementById('college').value.trim();
    const branch         = document.getElementById('branch').value.trim();
    const price          = document.getElementById('price').value.trim();
    const automationName = document.getElementById('automationName').value.trim();

    // Basic validation
    if (!name || !dimension || !college || !branch || !price || !automationName) {
        showMessage(msgEl, 'Please fill in all fields before submitting.', 'error');
        return;
    }

    // Loading state
    btn.disabled = true;
    btn.classList.add('loading');
    btn.classList.remove('error-state');
    btnText.textContent = 'Uploading…';
    msgEl.className = 'waiting-message';
    msgEl.textContent = 'This might take a moment, please wait.';

    try {
        const response = await fetch('/admin/assignment-upload', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                nameOfAssignment: name,
                dimensionOfAssignment: dimension,
                collegeOfAssignment: college,
                branchOfAssignment: branch,
                priceOfAssignment: price,
                automationName: automationName
            })
        });

        if (response.ok) {
            btn.classList.remove('loading');
            btn.disabled = false;
            btnText.textContent = '✓ Uploaded';
            showMessage(msgEl, 'Assignment uploaded successfully!', 'success');
            clearForm();

            setTimeout(() => {
                btnText.textContent = 'Submit';
                msgEl.textContent = '';
            }, 3000);

        } else {
            showError(btn, btnText, msgEl, 'Upload failed. Please try again.');
        }

    } catch (error) {
        console.error(error);
        showError(btn, btnText, msgEl, 'Could not reach server. Is it running?');
    }
}

function showError(btn, btnText, msgEl, message) {
    btn.classList.remove('loading');
    btn.classList.add('error-state');
    btn.disabled = false;
    btnText.textContent = 'Submit';
    showMessage(msgEl, message, 'error');

    setTimeout(() => {
        btn.classList.remove('error-state');
        msgEl.textContent = '';
        msgEl.className = 'waiting-message';
    }, 3000);
}

function showMessage(el, text, type) {
    el.className = 'waiting-message';
    if (type === 'error')   el.classList.add('error-text');
    if (type === 'success') el.classList.add('success-text');
    el.textContent = text;
}

function clearForm() {
    ['name', 'dimension', 'branch', 'price', 'automationName'].forEach(id => {
        document.getElementById(id).value = '';
    });
    document.getElementById('college').selectedIndex = 0;
}