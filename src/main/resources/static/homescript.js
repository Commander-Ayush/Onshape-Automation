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

document.querySelectorAll(".buy-btn").forEach(button => {

    button.onclick = async function () {

        const card = this.closest(".assignmentCard");

        const price = card.querySelector(".price-value span").innerText;
        const name = card.querySelector(".assignment-name").innerText;

        const response = await fetch("/api/payment/create-order", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({
                automationName: name,
                price: price
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

            handler: function (response) {

                alert("Payment successful " + response.razorpay_payment_id);

            }

        };

        const rzp = new Razorpay(options);

        rzp.open();
    };

});