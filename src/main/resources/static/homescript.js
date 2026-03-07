document.addEventListener('DOMContentLoaded', function () {
    filterCards();
});

function filterCards() {
    const selected  = document.getElementById('collegeFilter').value;
    const cards     = document.querySelectorAll('.assignmentCard');
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

    // Show or hide empty state
    if (emptyState) {
        emptyState.style.display = visibleCount === 0 ? 'flex' : 'none';
    }
}

document.querySelector('buy-btn').onclick = function () {

    const price = document.querySelector('.price-value').innerText;

    fetch('/api/payment/create-order?amount=1000&currency=INR', {
        method: 'POST'
    })
        .then(response => response.json())
        .then(order => {
            const options = {
                key: "rzp_test_SNts8h8YVvKcQU", // Replace with your Razorpay API Key
                amount: order.amount, // Amount in paise
                currency: order.currency,
                name: "Your Company",
                description: "Test Transaction",
                order_id: order.id,
                handler: function (response) {
                    alert("Payment Successful! Payment ID: " + response.razorpay_payment_id);
                },
                theme: {
                    color: "#3399cc"
                }
            };
            const rzp = new Razorpay(options);
            rzp.open();
        })
        .catch(err => console.error(err));
};