// Função para obter os parâmetros da URL
function getQueryParams() {
    const params = new URLSearchParams(window.location.search);
    const artistNames = params.getAll('nome'); // Obtém todos os valores de "nome"
    console.log('Artistas selecionados:', artistNames);
    return artistNames;
}

const userId = localStorage.getItem("userId");

if (userId) {
    console.log("ID do usuário:", userId);
} else {
    console.log("Usuário não está logado.");
}

// Função para enviar requisição ao tocar música
async function tocarMusica(musicaId) {
    try {
        if (!userId) {
            alert("Usuário não está logado.");
            return;
        }

        // Enviar requisição ao backend
        const response = await fetch(`http://localhost:8080/usuarios/gostaMusica?id=${userId}&musicaId=${musicaId}`, {
            method: 'POST',
        });

        if (!response.ok) {
            throw new Error('Erro ao registrar música ao usuário.');
        }


        if (!response.ok) {
            throw new Error('Erro ao registrar música tocada.');
        }

        alert('Música tocada com sucesso!');
    } catch (error) {
        console.error('Erro ao tocar música:', error);
        alert('Erro ao tocar música. Tente novamente mais tarde.');
    }
}

// Função para carregar músicas dos artistas selecionados
async function carregarMusicas() {
    const artistNames = getQueryParams();

    // Exibir mensagem se nenhum artista for selecionado
    if (!artistNames || artistNames.length === 0) {
        document.getElementById('musicas-container').innerHTML = `
            <div class="col-12 text-center">
                <p class="text-muted">Nenhum artista selecionado.</p>
            </div>`;
        return;
    }

    try {
        // Construir a query string com os nomes dos artistas
        const queryString = artistNames.map(name => `nome=${encodeURIComponent(name)}`).join('&');
        
        // Realizar a requisição para o backend
        const response = await fetch(`http://localhost:8080/usuarios/recomendacoes/musica?${queryString}`, {
            method: 'GET',
            mode: 'cors',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Falha ao buscar músicas.');
        }

        const musicas = await response.json();
        console.log('Músicas recebidas:', musicas);

        const musicasContainer = document.getElementById('musicas-container');
        musicasContainer.innerHTML = ""; // Limpa o container antes de adicionar as músicas

        // Verificar se há músicas retornadas
        if (Array.isArray(musicas) && musicas.length > 0) {
            // Iterar sobre as músicas e criar os cards
            musicas.forEach(musicaData => {
                const musica = musicaData.musica; // Acessando o objeto música
                const artistaNome = musicaData.artistaNome; // Nome do artista

                const col = document.createElement('div');
                col.classList.add('col-12', 'col-md-6', 'col-lg-4', 'mb-4');

                const card = document.createElement('div');
                card.classList.add('card', 'text-center');

                const cardBody = document.createElement('div');
                cardBody.classList.add('card-body');

                const titulo = document.createElement('h5');
                titulo.classList.add('card-title');
                titulo.textContent = musica.titulo || "Título Desconhecido";

                const artista = document.createElement('p');
                artista.classList.add('card-text');
                artista.textContent = artistaNome || "Artista Desconhecido";

                const botaoPlay = document.createElement('button');
                botaoPlay.classList.add('btn', 'btn-success', 'me-2');
                botaoPlay.textContent = "Curtir";

                // Adiciona evento para enviar requisição com o ID da música
                botaoPlay.addEventListener('click', () => {
                    tocarMusica(musica.id); // Passa o ID da música ao clicar
                });

                // Adiciona elementos ao card
                cardBody.appendChild(titulo);
                cardBody.appendChild(artista);
                cardBody.appendChild(botaoPlay);
                card.appendChild(cardBody);
                col.appendChild(card);

                // Adiciona o card ao container
                musicasContainer.appendChild(col);
            });
        } else {
            // Caso nenhuma música seja encontrada
            musicasContainer.innerHTML = `
                <div class="col-12 text-center">
                    <p class="text-muted">Nenhuma música encontrada para os artistas selecionados.</p>
                </div>`;
        }
    } catch (error) {
        // Exibe mensagem de erro no caso de falha
        console.error("Erro ao carregar músicas:", error);
        document.getElementById('musicas-container').innerHTML = `
            <div class="col-12 text-center">
                <p class="text-danger">Erro ao carregar músicas. Tente novamente mais tarde.</p>
            </div>`;
    }
}

// Função para carregar músicas recomendadas por usuários
async function carregarMusicasUsuarios() {
    try {
        // Realizar a requisição para o backend
        const response = await fetch(`http://localhost:8080/usuarios/recomendacoes/usuarios?id=${userId}`, {
            method: 'GET',
        });

        if (!response.ok) {
            throw new Error('Falha ao buscar músicas recomendadas por usuários.');
        }

        const musicas = await response.json();
        console.log('Músicas recomendadas por usuários:', musicas);

        const musicasContainer2 = document.getElementById('musicas-container2');
        musicasContainer2.innerHTML = ""; // Limpa o container antes de adicionar as músicas

        // Verificar se há músicas retornadas
        if (Array.isArray(musicas) && musicas.length > 0) {
            // Iterar sobre as músicas e criar os cards
            musicas.forEach(musicaData => {
                const musica = musicaData.musica; // Acessando o objeto música
                const artistaNome = musicaData.artistaNome; // Nome do artista

                const col = document.createElement('div');
                col.classList.add('col-12', 'col-md-6', 'col-lg-4', 'mb-4');

                const card = document.createElement('div');
                card.classList.add('card', 'text-center');

                const cardBody = document.createElement('div');
                cardBody.classList.add('card-body');

                const titulo = document.createElement('h5');
                titulo.classList.add('card-title');
                titulo.textContent = musica.titulo || "Título Desconhecido";

                const artista = document.createElement('p');
                artista.classList.add('card-text');
                artista.textContent = artistaNome || "Artista Desconhecido";

                const botaoPlay = document.createElement('button');
                botaoPlay.classList.add('btn', 'btn-success', 'me-2');
                botaoPlay.textContent = "Curtir";

                // Adiciona evento para enviar requisição com o ID da música
                botaoPlay.addEventListener('click', () => {
                    tocarMusica(musica.id); // Passa o ID da música ao clicar
                });

                // Adiciona elementos ao card
                cardBody.appendChild(titulo);
                cardBody.appendChild(artista);
                cardBody.appendChild(botaoPlay);

                card.appendChild(cardBody);
                col.appendChild(card);

                // Adiciona o card ao container
                musicasContainer2.appendChild(col);
            });
        } else {
            // Caso nenhuma música seja encontrada
            musicasContainer2.innerHTML = `
                <div class="col-12 text-center">
                    <p class="text-muted">Nenhuma música recomendada por outros usuários.</p>
                </div>`;
        }
    } catch (error) {
        // Exibe mensagem de erro no caso de falha
        console.error("Erro ao carregar músicas recomendadas por usuários:", error);
        document.getElementById('musicas-container2').innerHTML = `
            <div class="col-12 text-center">
                <p class="text-danger">Erro ao carregar músicas recomendadas por usuários. Tente novamente mais tarde.</p>
            </div>`;
    }
}

// Carrega as músicas ao carregar a página
document.addEventListener('DOMContentLoaded', () => {
    carregarMusicas(); // Carrega músicas dos artistas selecionados
    carregarMusicasUsuarios(); // Carrega músicas recomendadas por usuários
});

// Carrega as músicas ao carregar a página
//document.addEventListener('DOMContentLoaded', carregarMusicas);
