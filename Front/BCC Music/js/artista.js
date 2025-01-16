let currentPage = 0; // Página inicial
let pageSize = 30;   // Número de artistas por página
let totalPages = 0;  // Total de páginas
let currentGenre = ''; // Gênero atual selecionado
let selectedArtists = []; // Lista de IDs dos artistas selecionados

// Função para resetar seleções
function resetarSelecoes() {
    selectedArtists = []; // Limpa a lista de artistas selecionados
    const artistCards = document.querySelectorAll('.artist-card.active');
    artistCards.forEach(card => card.classList.remove('active')); // Remove a classe 'active'
}

// Função para carregar os gêneros
async function carregarGeneros() {
    try {
        const response = await fetch('http://localhost:8080/generos/generosapi', {
            method: 'GET',
            mode: 'cors',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Falha ao buscar gêneros.');
        }

        const generos = await response.json();
        console.log(generos);

        const generoContainer = document.getElementById('genero-buttons');
        generoContainer.innerHTML = "";

        const allGenresButton = document.createElement('button');
        allGenresButton.classList.add('btn', 'btn-outline-secondary', 'm-1');
        allGenresButton.textContent = "Todas as Músicas";
        allGenresButton.addEventListener('click', () => {
            currentGenre = '';
            currentPage = 0;
            carregarArtistas();
            totalPages = 0;
            atualizarBotoesNavegacao();
        });
        generoContainer.appendChild(allGenresButton);

        if (Array.isArray(generos)) {
            generos.forEach(genero => {
                const button = document.createElement('button');
                button.classList.add('btn', 'btn-outline-secondary', 'm-1');
                button.textContent = genero.nome;
                button.addEventListener('click', () => {
                    currentGenre = genero.nome;
                    currentPage = 0;
                    carregarArtistas();
                    totalPages = 0;
                    atualizarBotoesNavegacao();
                });
                generoContainer.appendChild(button);
            });
        } else {
            console.error("Dados de gêneros não estão no formato esperado:", generos);
        }
    } catch (error) {
        console.error("Erro ao carregar gêneros:", error);
    }
}

// Função para carregar artistas
async function carregarArtistas(nomeArtista = '') {
    try {
        const url = nomeArtista
            ? `http://localhost:8080/artistas/artistasapi/nome?nome=${nomeArtista}&page=${currentPage}&size=${pageSize}`
            : (currentGenre
                ? `http://localhost:8080/artistas/artistasapi/genero?genero=${currentGenre}&page=${currentPage}&size=${pageSize}`
                : `http://localhost:8080/artistas/artistasapi?page=${currentPage}&size=${pageSize}`);

        const response = await fetch(url, {
            method: 'GET',
            mode: 'cors',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Falha ao buscar artistas.');
        }

        const data = await response.json();
        console.log("Dados recebidos:", data);

        const artistas = data.artistas;
        totalPages = data.totalPages;
        currentPage = data.currentPage;

        const artistasContainer = document.getElementById('artist-cards');
        artistasContainer.innerHTML = "";

        if (Array.isArray(artistas)) {
            artistas.forEach(artistaObj => {
                const artista = artistaObj.artista;

                const col = document.createElement('div');
                col.classList.add('col-6', 'col-md-3', 'mb-4');

                const card = document.createElement('div');
                card.classList.add('artist-card', 'text-center');
                card.dataset.id = artista.id;

               
                card.addEventListener('click', () => selecionarArtista(card));

                const img = document.createElement('img');
                img.src = artista.picture ? artista.picture : "https://via.placeholder.com/150";
                img.alt = artista.nome || "Artista Desconhecido";
                img.classList.add('img-fluid', 'rounded-circle');

                const p = document.createElement('p');
                p.textContent = artista.nome || "Artista Desconhecido";

                
                img.addEventListener('click', event => {
                    event.stopPropagation();
                    selecionarArtista(card);
                });

                card.appendChild(img);
                card.appendChild(p);
                col.appendChild(card);
                artistasContainer.appendChild(col);
            });

            exibirQuantidadePaginas();
            atualizarBotoesNavegacao();
        } else {
            console.error('A resposta da API não contém um array de artistas:', artistas);
        }

        resetarSelecoes();

    } catch (error) {
        console.error("Erro ao carregar artistas:", error);
    }
}

function selecionarArtista(card) {
    const artistId = card.dataset.id;

    if (card.classList.contains('active')) {
        card.classList.remove('active');
        selectedArtists = selectedArtists.filter(id => id !== artistId);
    } else {
        if (selectedArtists.length < 5) {
            card.classList.add('active');
            selectedArtists.push(artistId);
        }
    }

    if (selectedArtists.length === 5) {
        const queryString = selectedArtists.map(id => `id=${id}`).join('&');
        window.location.href = `musics.html?${queryString}`;
    }
}

function exibirQuantidadePaginas() {
    const paginasContainer = document.getElementById('quantidade-paginas');
    paginasContainer.textContent = `Página ${currentPage + 1} de ${totalPages}`;
}

function navegarPagina(direcao) {
    if (direcao === 'anterior' && currentPage > 0) {
        currentPage--;
    } else if (direcao === 'proximo' && currentPage < totalPages - 1) {
        currentPage++;
    }
    carregarArtistas();
}

function atualizarBotoesNavegacao() {
    const prevButton = document.getElementById('prevButton');
    const nextButton = document.getElementById('nextButton');

    if (prevButton && nextButton) {
        prevButton.disabled = currentPage === 0;
        nextButton.disabled = currentPage === totalPages - 1;
    } else {
        console.error('Botões de navegação não encontrados.');
    }
}

document.querySelector('.search-bar').addEventListener('input', (event) => {
    const nomeArtista = event.target.value.trim();
    currentPage = 0;
    carregarArtistas(nomeArtista);
    totalPages = 0;
    atualizarBotoesNavegacao();
});

document.addEventListener('DOMContentLoaded', () => {
    carregarGeneros();
    carregarArtistas();
});
