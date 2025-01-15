let selectedArtists = 0;

document.querySelectorAll('.artist-card').forEach(card => {
    card.addEventListener('click', () => {
        card.classList.toggle('active');
        
        if (card.classList.contains('active')) {
            selectedArtists++;
        } else {
            selectedArtists--;
        }

        if (selectedArtists >= 3) {
            document.getElementById('next-page-btn').classList.remove('disabled');
            document.getElementById('next-page-btn').href = "index.html";
        } else {
            document.getElementById('next-page-btn').classList.add('disabled');
            document.getElementById('next-page-btn').href = "#";
        }
    });
});
