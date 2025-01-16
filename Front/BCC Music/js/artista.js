let currentPage = 0; // Página inicial
let pageSize = 30;   // Número de artistas por página
let totalPages = 0;  // Total de páginas
let currentGenre = ''; // Gênero atual selecionado

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

        // Botão para exibir todos os artistas, sem filtro de gênero
        const allGenresButton = document.createElement('button');
        allGenresButton.classList.add('btn', 'btn-outline-secondary', 'm-1');
        allGenresButton.textContent = "Todas as Músicas";
        allGenresButton.addEventListener('click', () => {
            // Ao clicar, carrega os artistas sem filtro de gênero
            currentGenre = ''; // Reseta o filtro de gênero
            currentPage = 0;   // Reinicia a página ao trocar de gênero
            carregarArtistas(); // Carrega os artistas sem filtro
            totalPages = 0;     // Reinicia a quantidade de páginas
            atualizarBotoesNavegacao(); // Atualiza os botões de navegação
        });
        generoContainer.appendChild(allGenresButton);

        if (Array.isArray(generos)) {
            generos.forEach(genero => {
                const button = document.createElement('button');
                button.classList.add('btn', 'btn-outline-secondary', 'm-1');
                button.textContent = genero.nome;
                button.addEventListener('click', () => {
                    // Ao clicar, filtra os artistas pelo gênero
                    currentGenre = genero.nome;
                    currentPage = 0;   // Reinicia a página ao mudar de gênero
                    carregarArtistas(); // Carrega os artistas filtrados
                    totalPages = 0;     // Reinicia a quantidade de páginas
                    atualizarBotoesNavegacao(); // Atualiza os botões de navegação
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

// Função para carregar artistas com base no nome do artista
async function carregarArtistas(nomeArtista = '') {
    try {
        // Se um nome de artista for fornecido, filtra os artistas por esse nome
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
        totalPages = data.totalPages; // Atualiza o número total de páginas
        currentPage = data.currentPage; // Página atual

        const artistasContainer = document.getElementById('artist-cards');
        artistasContainer.innerHTML = ""; // Limpa o container antes de adicionar os novos artistas

        if (Array.isArray(artistas)) {
            artistas.forEach(artistaObj => {
                const artista = artistaObj.artista;

                const col = document.createElement('div');
                col.classList.add('col-6', 'col-md-3', 'mb-4');

                const card = document.createElement('div');
                card.classList.add('artist-card', 'text-center');

                const img = document.createElement('img');
                img.src = artista.picture ? artista.picture : "https://via.placeholder.com/150";
                img.alt = artista.nome || "Artista Desconhecido";
                img.classList.add('img-fluid', 'rounded-circle');

                const p = document.createElement('p');
                p.textContent = artista.nome || "Artista Desconhecido";

                card.appendChild(img);
                card.appendChild(p);
                col.appendChild(card);
                artistasContainer.appendChild(col);
            });

            // Exibe a quantidade de páginas
            exibirQuantidadePaginas();
            
            // Atualizar visibilidade dos botões de navegação
            atualizarBotoesNavegacao();
        } else {
            console.error('A resposta da API não contém um array de artistas:', artistas);
        }
    } catch (error) {
        console.error("Erro ao carregar artistas:", error);
    }
}

// Função para buscar artistas ao digitar na barra de pesquisa
document.querySelector('.search-bar').addEventListener('input', (event) => {
    const nomeArtista = event.target.value.trim(); // Pega o valor digitado na pesquisa
    currentPage = 0;  // Reinicia a página ao fazer uma nova busca
    carregarArtistas(nomeArtista); // Chama a função de carregar artistas com o nome filtrado
    totalPages = 0;  // Reinicia o total de páginas
    atualizarBotoesNavegacao(); // Atualiza os botões de navegação
});

// Função para exibir a quantidade de páginas
function exibirQuantidadePaginas() {
    const paginasContainer = document.getElementById('quantidade-paginas');
    paginasContainer.textContent = `Página ${currentPage + 1} de ${totalPages}`;
}

// Função para navegar entre as páginas
function navegarPagina(direcao) {
    if (direcao === 'anterior' && currentPage > 0) {
        currentPage--; // Vai para a página anterior
    } else if (direcao === 'proximo' && currentPage < totalPages - 1) {
        currentPage++; // Vai para a próxima página
    }
    carregarArtistas(); // Carrega a página correspondente
}

// Função para atualizar a visibilidade dos botões de navegação
function atualizarBotoesNavegacao() {
    const prevButton = document.getElementById('prevButton');
    const nextButton = document.getElementById('nextButton');

    // Verifica se os botões existem no DOM antes de alterar suas propriedades
    if (prevButton && nextButton) {
        // Habilitar/desabilitar botões com base na página atual
        prevButton.disabled = currentPage === 0; // Desabilitar o botão "Anterior" na primeira página
        nextButton.disabled = currentPage === totalPages - 1; // Desabilitar o botão "Próximo" na última página
    } else {
        console.error('Botões de navegação não encontrados.');
    }
}

// Função para validar a URL
function isValidUrl(string) {
    try {
        new URL(string);
        return true;
    } catch (_) {
        return false;  
    }
}

// Função para habilitar/desabilitar o botão "Próximo"
let selectedArtists = 0;

document.addEventListener('click', event => {
    if (event.target && event.target.classList.contains('artist-card')) {
        const card = event.target;
        card.classList.toggle('active');

        if (card.classList.contains('active')) {
            selectedArtists++;
        } else {
            selectedArtists--;
        }

        const nextButton = document.getElementById('next-page-btn');

        if (selectedArtists >= 3) {
            nextButton.classList.remove('disabled');
            nextButton.href = "index.html";
        } else {
            nextButton.classList.add('disabled');
            nextButton.href = "#";
        }
    }
});

// Chama as funções para carregar os dados ao carregar a página
document.addEventListener('DOMContentLoaded', () => {
    carregarGeneros();
    carregarArtistas();  // Carrega os artistas sem filtro de gênero inicialmente
});
