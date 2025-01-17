async function carregarMusicas() {
    try {
        const userId = localStorage.getItem("userId");
        console.log("ID do usuário:", userId);

        if (!userId) {
            throw new Error('Usuário não está logado.');
        }

        const response = await fetch(`http://localhost:8080/usuarios/musicasAdicionadas?id=${userId}`, {
            method: 'GET',
        });

        if (!response.ok) {
            throw new Error('Erro ao buscar músicas.');
        }

        const musicas = await response.json(); // Extrai os dados JSON
        console.log('Músicas recebidas:', musicas);

        const musicasContainer = document.getElementById('musicas-container');
        musicasContainer.innerHTML = ''; // Limpa o container

        musicas.forEach(musica => {
            const col = document.createElement('div');
            col.classList.add('col-md-4', 'mb-4');

            const card = `
                <div class="card">
                    <img src="${musica.albumArt || 'https://via.placeholder.com/300x200'}" class="card-img-top" alt="Album Art">
                    <div class="card-body">
                        <h5 class="card-title">${musica.titulo}</h5>
                        <p class="card-text">${musica.artista || 'Artista Desconhecido'}</p>
                        <a href="${musica.preview || '#'}" class="btn btn-primary" target="_blank">Play</a>
                        <button class="btn btn-danger mt-2 btn-excluir" data-musica-id="${musica.id}">Excluir</button>
                    </div>
                </div>
            `;

            col.innerHTML = card;
            musicasContainer.appendChild(col);
        });

        // Adiciona evento aos botões "Excluir"
        const excluirButtons = document.querySelectorAll('.btn-excluir');
        excluirButtons.forEach(button => {
            button.addEventListener('click', async (event) => {
                const musicaId = event.target.getAttribute('data-musica-id');
                await excluirMusica(musicaId);
                carregarMusicas(); // Recarrega a lista após excluir
            });
        });

    } catch (error) {
        console.error('Erro ao carregar músicas:', error);

        const musicasContainer = document.getElementById('musicas-container');
        musicasContainer.innerHTML = `
            <div class="col-12 text-center">
                <p class="text-danger">Erro ao carregar músicas. Tente novamente mais tarde.</p>
            </div>
        `;
    }
}

async function excluirMusica(musicaId) {
    try {
        const userId = localStorage.getItem("userId");

        if (!userId) {
            alert('Usuário não está logado.');
            return;
        }

        const response = await fetch(`http://localhost:8080/usuarios/removerMusica?id=${userId}&musicaId=${musicaId}`, {
            method: 'DELETE',
        });

        if (!response.ok) {
            throw new Error('Erro ao excluir a música.');
        }

        alert('Música excluída com sucesso!');
    } catch (error) {
        console.error('Erro ao excluir música:', error);
        alert('Erro ao excluir a música. Tente novamente mais tarde.');
    }
}

// Chama a função ao carregar a página
document.addEventListener('DOMContentLoaded', carregarMusicas);
