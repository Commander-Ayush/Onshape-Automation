document.addEventListener('DOMContentLoaded', function () {
    filterCards();
});

function filterCards() {
    const selected   = document.getElementById('collegeFilter').value;
    const cards      = document.querySelectorAll('.assignmentCard');
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

// ── Modal state ───────────────────────────────────────
let currentAssignment = {};

function openModal(button) {
    const card = button.closest('.assignmentCard');

    const priceRaw = card.querySelector('.price-value span').innerText.trim(); // just the number

    currentAssignment = {
        name: card.querySelector('.assignment-name').innerText.trim(),
        scriptFileName:    card.querySelector('#script-file-name').textContent.trim(),
        college: card.querySelector('.college-badge').textContent.trim(),
        price:   priceRaw,
        image:   card.querySelector('.img-placeholder img').src,
    };

    document.getElementById('modalName').textContent    = currentAssignment.name;
    document.getElementById('modalCollege').textContent = currentAssignment.college;
    document.getElementById('modalPrice').textContent   = '₹' + currentAssignment.price;
    document.getElementById('modalImage').src           = currentAssignment.image;

    document.getElementById('referralInput').value        = '';
    document.getElementById('referralStatus').textContent = '';
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
    const referral = document.getElementById('referralInput').value.trim();
    closeModal();
    initiateRazorpay(currentAssignment, referral);
}

async function initiateRazorpay(assignment, referral) {
    const response = await fetch("/api/payment/create-order", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            assignmentName: assignment.name,
            scriptFileName: assignment.scriptFileName,
            price: assignment.price,
            referralCode: referral
        })
    });

    const order = await response.json();

    const options = {
        key: "rzp_test_SNts8h8YVvKcQU",
        amount: order.price * 100,
        currency: "INR",
        name: "Graphics Auto",
        description: "Order for " + order.automationName,
        order_id: order.razorpayOrderId,
        handler: async function (response) {
            try {
                const verifyResponse = await fetch("/api/payment/verify", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        razorpayPaymentId: response.razorpay_payment_id,
                        razorpayOrderId:   response.razorpay_order_id,
                        razorpaySignature: response.razorpay_signature
                    })
                });

                const result = await verifyResponse.json();

                if (result.status === "verified") {
                    alert("✅ Payment verified! ID: " + response.razorpay_payment_id);
                    // optional: window.location.href = "/success";
                } else {
                    alert("❌ Payment verification failed. Please contact support.");
                }
            } catch (err) {
                console.error("Verification error:", err);
                alert("Something went wrong during verification.");
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
        body: JSON.stringify({ referralCode: referralCode })
    });

    try {
        if (response.ok) {
            const data = await response.json();
            const discount = data.discount;

            currentAssignment.price = currentAssignment.price - discount;
            document.getElementById('modalPrice').textContent = '₹' + currentAssignment.price;
            document.getElementById('apply').innerText = '✓ Code applied! ₹' + discount + ' off';
            document.getElementById('referralInput').disabled = true;
            document.getElementById('referralInput').value='';
            this.disabled=true;

        } else {
            document.getElementById('referralStatus').innerText = "This referral code doesn't exist";
            this.disabled = false;
        }
    } catch (e) {
        document.getElementById('referralStatus').innerText = "Something went wrong on the server side";
        this.disabled = false;
        console.log(e.message)
    }

});

document.getElementById("referralInput").addEventListener("keypress", function () {
    document.getElementById('referralStatus').innerText =
        "";
    document.getElementById('apply').disabled = false;
})