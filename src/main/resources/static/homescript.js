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