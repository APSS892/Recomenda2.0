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
});/*
const API_URL = 'http://localhost:8080/artistas';

let currentScrollPosition = 0;

async function fetchArtistas() {
    try {
        const response = await fetch(API_URL);
        const artistas = await response.json();

        const container = document.getElementById('carousel-container');
        container.innerHTML = '';

        artistas.forEach((artista) => {
            const artistElement = document.createElement('div');
            artistElement.innerHTML = `
                <img src="${artista.fotoUrl}" alt="${artista.nome}" />
                <p>${artista.nome}</p>
            `;
            container.appendChild(artistElement);
        });
    } catch (error) {
        console.error('Erro ao buscar artistas:', error);
    }
}

function nextSlide() {
    const container = document.getElementById('carousel-container');
    currentScrollPosition += container.offsetWidth;
    container.scrollLeft = currentScrollPosition;
}

function previousSlide() {
    const container = document.getElementById('carousel-container');
    currentScrollPosition -= container.offsetWidth;
    container.scrollLeft = currentScrollPosition;
}

// Chama a função para carregar os artistas quando a página carregar
window.onload = fetchArtistas;
*/
const API_BASE_URL = 'http://localhost:8080/artistas'; 

async function fetchArtistas() {
    try {
        const response = await fetch(API_BASE_URL);

        if (!response.ok) {
            throw new Error(`Erro na requisição: ${response.status} ${response.statusText}`);
        }

        const artistas = await response.json();
        console.log('Artistas recebidos:', artistas); 

        const carouselTrack = document.querySelector('.carousel-track');
        if (!carouselTrack) {
            console.error('Elemento .carousel-track não encontrado no DOM.');
            return;
        }

        carouselTrack.innerHTML = '';

        artistas.forEach(item => {
            const artista = item.artista || {};
            const nome = artista.nome || 'Artista Desconhecido';
            const picture = artista.picture || 'https://via.placeholder.com/150';
            const generos = artista.generos?.length > 0
                ? artista.generos.map(g => g.nome).join(', ')
                : 'Sem gênero';

            console.log(`Processando artista: Nome: ${nome}, Imagem: ${picture}`);

            const card = document.createElement('div');
            card.classList.add('artist-card');

            card.innerHTML = `
                <img src="${picture}" alt="${nome}" class="artist-image">
                <h3 class="artist-name">${nome}</h3>
       
            `;

            carouselTrack.appendChild(card);
        });
    } catch (error) {
        console.error('Erro ao buscar artistas:', error);
    }
}

function scrollCarousel(direction) {
    const carouselTrack = document.querySelector('.carousel-track');
    const scrollAmount = 300;
    if (carouselTrack) {
        if (direction === 'left') {
            carouselTrack.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
        } else {
            carouselTrack.scrollBy({ left: scrollAmount, behavior: 'smooth' });
        }
    } else {
        console.error('Elemento .carousel-track não encontrado no DOM.');
    }
}

document.addEventListener('DOMContentLoaded', fetchArtistas);

