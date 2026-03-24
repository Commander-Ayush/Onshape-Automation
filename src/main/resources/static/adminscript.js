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
