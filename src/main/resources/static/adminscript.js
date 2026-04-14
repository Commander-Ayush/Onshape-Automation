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
    const scriptFile = document.getElementById('script-name').files[0];

    if (!name || !dimension || !college || !branch || !price || !imageFile || !scriptFile) {
        showMessage(msgEl, 'Please fill in all fields before submitting.', 'error');
        return;
    }

    btn.disabled = true;
    btn.classList.add('loading');
    btnText.textContent = 'Uploading…';
    msgEl.textContent = 'This might take a moment, please wait.';

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

async function loadFailedOrders() {
    try {
        const response = await fetch("/admin/failed-orders");
        const orders = await response.json();

        const errorList = document.getElementById("errorList");
        const errorCount = document.getElementById("errorCount");
        const emptyMsg = document.getElementById("errorListEmpty");

        errorCount.textContent = orders.length + " Failure" + (orders.length !== 1 ? "s" : "");

        if (orders.length === 0) {
            emptyMsg.style.display = "flex";
            errorList.querySelectorAll(".error-item").forEach(el => el.remove());
            return;
        }

        emptyMsg.style.display = "none";
        errorList.querySelectorAll(".error-item").forEach(el => el.remove());

        orders.forEach(order => {
            const item = document.createElement("div");
            item.className = "error-item";
            item.innerHTML = `
                <div class="error-item-header">
                    <span class="error-script">${order.orderedAutomation}</span>
                    <button class="error-delete-btn" onclick="deleteFailedOrder(${order.id})">✕</button>
                </div>
                <div class="error-detail">
                    <span>📧 ${order.customerEmail}</span>
                    <span>🔑 ${order.customerPass}</span>
                </div>
                <div class="error-reason">${order.failureReason}</div>
            `;
            errorList.appendChild(item);
        });

    } catch (e) {
        console.error("Failed to load failed orders:", e);
    }
}

async function deleteFailedOrder(id) {
    try {
        await fetch("/admin/failed-orders/" + id, { method: "DELETE" });
        await loadFailedOrders();
    } catch (e) {
        console.error("Failed to delete order:", e);
    }
}

async function clearLogs() {
    try {
        await fetch("/admin/failed-orders", { method: "DELETE" });
        await loadFailedOrders();
    } catch (e) {
        console.error("Failed to clear logs:", e);
    }
}

function redirectToHome() {
    window.location.href = "/home";
}

document.addEventListener("DOMContentLoaded", () => {
    loadFailedOrders();
    setInterval(() => { loadFailedOrders(); }, 30000);
});