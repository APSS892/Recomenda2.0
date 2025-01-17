let currentPage = 0;
let pageSize = 30;
let totalPages = 0;
let currentGenre = '';
let selectedArtists = [];
let searchQuery = '';

// Função para resetar seleções
function resetarSelecoes() {
    selectedArtists = [];
    const artistCards = document.querySelectorAll('.artist-card.active');
    artistCards.forEach(card => card.classList.remove('active'));
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
    } catch (error) {
        console.error("Erro ao carregar gêneros:", error);
    }
}

// Função para carregar artistas
async function carregarArtistas() {
    try {
        const url = searchQuery
            ? `http://localhost:8080/artistas/artistasapi/nome?nome=${searchQuery}&page=${currentPage}&size=${pageSize}`
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
        const artistas = data.artistas;
        totalPages = data.totalPages;
        currentPage = data.currentPage;

        const artistasContainer = document.getElementById('artist-cards');
        artistasContainer.innerHTML = "";

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

            const p = document.createElement('p');
            p.textContent = artista.nome || "Artista Desconhecido";

            // Marcar o artista como selecionado, caso já tenha sido selecionado
            if (selectedArtists.includes(artista.nome)) {
                card.classList.add('active');
            }

            card.appendChild(img);
            card.appendChild(p);
            col.appendChild(card);
            artistasContainer.appendChild(col);
        });

        exibirQuantidadePaginas();
        atualizarBotoesNavegacao();

    } catch (error) {
        console.error("Erro ao carregar artistas:", error);
    }
}

// Função para atualizar a lista de artistas selecionados
function atualizarListaSelecionados() {
    const listaSelecionados = document.getElementById('selected-artists-ul');
    listaSelecionados.innerHTML = '';

    selectedArtists.forEach(artist => {
        const li = document.createElement('li');
        li.textContent = artist;
        listaSelecionados.appendChild(li);
    });
}

// Função de exibição da quantidade de páginas
function exibirQuantidadePaginas() {
    const paginasContainer = document.getElementById('quantidade-paginas');
    paginasContainer.textContent = `Página ${currentPage + 1} de ${totalPages}`;
}

// Função para navegação entre páginas
function navegarPagina(direcao) {
    if (direcao === 'anterior' && currentPage > 0) {
        currentPage--;
    } else if (direcao === 'proximo' && currentPage < totalPages - 1) {
        currentPage++;
    }
    carregarArtistas();
}

// Função para atualizar os botões de navegação
function atualizarBotoesNavegacao() {
    const prevButton = document.getElementById('prevButton');
    const nextButton = document.getElementById('nextButton');

    prevButton.disabled = currentPage === 0;
    nextButton.disabled = currentPage === totalPages - 1;
}

// Lidar com a pesquisa por nome de artista
document.querySelector('.search-bar').addEventListener('input', (event) => {
    searchQuery = event.target.value.trim();
    currentPage = 0;
    carregarArtistas();
    totalPages = 0;
    atualizarBotoesNavegacao();
});

// Função para selecionar/desmarcar um artista
function selecionarArtista(card) {
    const artistName = card.querySelector('p').textContent;

    if (card.classList.contains('active')) {
        card.classList.remove('active');
        selectedArtists = selectedArtists.filter(name => name !== artistName);
    } else {
        if (selectedArtists.length < 5) {
            card.classList.add('active');
            selectedArtists.push(artistName);
        }
    }

    atualizarListaSelecionados();

    if (selectedArtists.length === 5) {
        const queryString = selectedArtists.map(name => `nome=${encodeURIComponent(name)}`).join('&');
        window.location.href = `musics.html?${queryString}`;
    }
}

// Carregar gêneros e artistas ao iniciar a página
document.addEventListener('DOMContentLoaded', () => {
    carregarGeneros();
    carregarArtistas();
});
