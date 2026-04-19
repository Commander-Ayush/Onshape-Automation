document.addEventListener('DOMContentLoaded', filterCards);

function filterCards() {
    const selected = document.getElementById('collegeFilter').value;
    const cards = document.querySelectorAll('.assignmentCard');
    const emptyState = document.getElementById('emptyState');

    let visibleCount = 0;

    cards.forEach(function (card) {
        const college = card.getAttribute('data-college');
        if (selected === 'ALL' || college === selected) {
            card.classList.remove('hidden');
            visibleCount++;
        } else {
            card.classList.add('hidden');
        }
    });

    if (emptyState) {
        emptyState.style.display = visibleCount === 0 ? 'flex' : 'none';
    }
}
function openReceiptModel(razorpayOrderId, customersEmail) {

    document.getElementById('razorpayOrderID').innerText = razorpayOrderId;
    document.getElementById('confirmed-email').innerText = customersEmail;

    document.getElementById('receiptModal').classList.add('active');

    return new Promise((resolve) => {
        document.getElementById('upiSubmit').onclick = function () {
            this.disabled = true;
            const usersUpiId = document.getElementById('upiInput').value.trim();
            document.getElementById('upiInput').disabled = true;

            const msg = document.getElementById('earn-message');
            msg.innerText = '🤍 Thank you! We\'ll transfer your earnings to this UPI soon.';
            setTimeout(() => msg.classList.add('visible'), 50);

            setTimeout(() => {
                closeReceiptModal();
                resolve(usersUpiId);
            }, 2200);
        };
    });
}


function closeReceiptModal() {
    document.getElementById('receiptModal').classList.remove('active');
    document.body.style.overflow = '';
    document.querySelector('header').classList.remove('modal-open');
}

document.addEventListener('keydown', e => {
    if (e.key === 'Escape') closeReceiptModal();
});

// ── Modal state ───────────────────────────────────────
let currentAssignment = {};

function openModal(button) {
    const card = button.closest('.assignmentCard');

    currentAssignment = {
        name: card.querySelector('.assignment-name').innerText.trim(),
        scriptFileName: card.querySelector('#script-file-name').textContent.trim(),
        college: card.querySelector('.college-badge').textContent.trim(),
        price: parseInt(card.querySelector('.price-value span').innerText.trim()), // ← add parseInt
        image: card.querySelector('.img-placeholder img').src,
        referralCode: ''
    };

    document.getElementById('modalName').textContent = currentAssignment.name;
    document.getElementById('modalCollege').textContent = currentAssignment.college;
    document.getElementById('modalPrice').textContent = '₹' + currentAssignment.price;
    document.getElementById('modalImage').src = currentAssignment.image;
    document.getElementById('referralInput').value = '';
    document.getElementById('referralInput').disabled = false;
    document.getElementById('referralStatus').textContent = '';
    document.getElementById('apply').disabled = false;
    document.getElementById('apply').innerText = 'Apply';

    document.getElementById('purchaseModal').classList.add('active');
    document.body.style.overflow = 'hidden';
    document.querySelector('header').classList.add('modal-open');
}

function closeModal() {
    document.getElementById('purchaseModal').classList.remove('active');
    document.body.style.overflow = '';
    document.querySelector('header').classList.remove('modal-open');
}

document.addEventListener('keydown', e => {
    if (e.key === 'Escape') closeModal();
});

function handleBuyNow() {
    const referral = currentAssignment.referralCode || '';
    closeModal();
    initiateRazorpay(currentAssignment, referral);
}

async function initiateRazorpay(assignment, referral) {

    const scriptName = assignment.scriptFileName.substring(
        0,
        assignment.scriptFileName.lastIndexOf(".")
    );

    // STEP 1: Create order
    const backendAPIResponse = await fetch("/api/payment/create-order", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            assignmentName: assignment.name,
            scriptFileName: scriptName,
            price: assignment.price,
            referralCode: referral
        })
    });

    console.log("create-order-initiated");

    const order = await backendAPIResponse.json();

    console.log(order);

    const options = {
        key: "rzp_live_SeqEOi57o1J9t5",
        amount: order.price * 100,
        currency: "INR",
        name: "Graphics Auto",
        description: "Order for " + order.assignmentName,
        order_id: order.razorpayOrderId,

        handler: async function (response) {

            const razorpayPaymentId = response.razorpay_payment_id;
            const razorpayOrderId = response.razorpay_order_id;
            const razorpaySignature = response.razorpay_signature;

            const usersUpiId = await openReceiptModel(razorpayOrderId, order.userEmail);

            try {
                const verifyResponse = await fetch("/api/payment/verify", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        razorpayPaymentId,
                        razorpayOrderId,
                        razorpaySignature
                    })
                });

                console.log("verification entities sent");

                const result = await verifyResponse.json();

                if (result.status === "verified") {
                    // STEP 3: Save order
                    const saveNExeResponse = await fetch("/api/payment/save-order", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({
                            assignmentName: assignment.name,
                            scriptFileName: scriptName,
                            price: assignment.price,
                            referralCode: referral,
                            razorpayPaymentId,
                            razorpayOrderId,
                            usersUpiId
                        })
                    });

                    const executionResponse = await saveNExeResponse.json();

                } else {
                    alert("Payment verification failed. Please contact support.");
                }

            } catch (err) {
                console.error("Payment error:", err);

                await fetch("/api/admin/log-error", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        endpoint: "payment-handler",
                        errorMessage: err.message,
                        stackTrace: err.stack
                    })
                });
            }
        }
    };

    const rzp = new Razorpay(options);
    rzp.open();
}

document.getElementById("apply").addEventListener("click", async function () {
    this.disabled = true;

    const referralCode = document.getElementById('referralInput').value.trim();


    const response = await fetch("/api/payment/referralCode", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ referralCode, price: currentAssignment.price  })
    });

    try {
        if (response.ok) {
            const data = await response.json();
            const discount = data.discount;

            currentAssignment.price -= discount;
            currentAssignment.referralCode = referralCode;
            document.getElementById('modalPrice').textContent = '₹' + currentAssignment.price;
            document.getElementById('apply').innerText = '✓ Code applied! ₹' + discount + ' off';
            document.getElementById('referralInput').disabled = true;
            document.getElementById('referralInput').value = '';
        } else {
            document.getElementById('referralStatus').innerText = "This referral code doesn't exist";
            this.disabled = false;
        }
    } catch (e) {
        document.getElementById('referralStatus').innerText = "Something went wrong on the server side";
        this.disabled = false;
        console.log(e.message);
    }
});

document.getElementById("referralInput").addEventListener("keypress", function () {
    document.getElementById('referralStatus').innerText = '';
    document.getElementById('apply').disabled = false;
});

