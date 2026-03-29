async function uploadAssignment() {
    console.log("Uploading Assignment...");
    const btn = document.getElementById('submitBtn');
    const btnText = document.getElementById('btnText');
    const msgEl = document.getElementById('waitingMessage');

    const name = document.getElementById('name').value.trim();
    const dimension = document.getElementById('dimension').value.trim();
    const college = document.getElementById('college').value.trim();
    const branch = document.getElementById('branch').value.trim();
    const price = document.getElementById('price').value.trim();
    const imageFile = document.getElementById('image').files[0];
    const scriptFile = document.getElementById('script-name').files[0]; // ← note the fix here too

    console.log("Data loaded into variables...");

    if (!name || !dimension || !college || !branch || !price || !imageFile || !scriptFile) {
        showMessage(msgEl, 'Please fill in all fields before submitting.', 'error');
        return;
    }

    btn.disabled = true;
    btn.classList.add('loading');
    btnText.textContent = 'Uploading…';
    msgEl.textContent = 'This might take a moment, please wait.';

    console.log("Button Execution started...");

    const formData = new FormData();
    formData.append('image', imageFile);
    formData.append('nameOfImage', imageFile.name);
    formData.append('scriptFile', scriptFile);
    formData.append('nameOfAssignment', name);
    formData.append('dimensionOfAssignment', dimension);
    formData.append('collegeOfAssignment', college);
    formData.append('branchOfAssignment', branch);
    formData.append('priceOfAssignment', price);

    try {
        const response = await fetch('/admin/assignment-upload', {
            method: 'POST',
            body: formData
            // DO NOT set Content-Type header - browser sets it automatically
        });

        if (response.ok) {
            btn.classList.remove('loading');
            btn.disabled = false;
            btnText.textContent = '✓ Uploaded';
            showMessage(msgEl, 'Assignment uploaded successfully!', 'success');
            clearForm();
            setTimeout(() => { btnText.textContent = 'Submit'; msgEl.textContent = ''; }, 3000);
        } else {
            showError(btn, btnText, msgEl, 'Upload failed. Please try again.');
        }
    } catch (error) {
        console.error(error);
        showError(btn, btnText, msgEl, 'Could not reach server. Is it running?');
    }
}

async function loadErrors() {
    try {
        const response = await fetch('/admin/errors');
        const errors = await response.json();

        const container = document.getElementById('errorList');
        const emptyState = document.getElementById('errorListEmpty');
        const countBadge = document.getElementById('errorCount');

        countBadge.textContent = errors.length + ' error' + (errors.length === 1 ? '' : 's');

        if (errors.length === 0) {
            emptyState.style.display = 'flex';
            countBadge.classList.remove('has-errors');
            return;
        }

        emptyState.style.display = 'none';
        countBadge.classList.add('has-errors');

        // clear previous items before re-rendering
        container.querySelectorAll('.error-item').forEach(el => el.remove());

        errors.forEach(err => {
            const item = document.createElement('div');
            item.className = 'error-item';
            item.innerHTML = `
                <div class="error-item-header">
                    <span class="error-endpoint">${err.endpoint ?? '—'}</span>
                    <span class="error-timestamp">${new Date(err.timestamp).toLocaleString()}</span>
                </div>
                <span class="error-message">${err.errorMessage ?? 'Unknown error'}</span>
            `;
            container.appendChild(item);
        });

    } catch (e) {
        console.error('Failed to load errors:', e);
    }
}

async function clearErrors() {
    try {
        await fetch('/admin/errors/clear', { method: 'DELETE' });
        document.querySelectorAll('.error-item').forEach(el => el.remove());
        document.getElementById('errorListEmpty').style.display = 'flex';
        document.getElementById('errorCount').textContent = '0 errors';
        document.getElementById('errorCount').classList.remove('has-errors');
    } catch (e) {
        console.error('Failed to clear errors:', e);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    loadErrors();
});

function redirectToHome(){
    window.location.href = "/home";
}
