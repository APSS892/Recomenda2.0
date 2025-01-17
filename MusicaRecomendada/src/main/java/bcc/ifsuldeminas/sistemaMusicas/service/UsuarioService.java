package bcc.ifsuldeminas.sistemaMusicas.service;
import org.neo4j.driver.*;
import bcc.ifsuldeminas.sistemaMusicas.model.entities.Musica;
import bcc.ifsuldeminas.sistemaMusicas.model.entities.Playlist;
import bcc.ifsuldeminas.sistemaMusicas.model.entities.Usuario;
import bcc.ifsuldeminas.sistemaMusicas.repository.MusicaRepository;
import bcc.ifsuldeminas.sistemaMusicas.repository.UsuarioRepository;
import org.neo4j.driver.types.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    @Autowired
    private MusicaRepository musicaRepository;
    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, Driver driver, Neo4jClient neo4jClient) {
        this.usuarioRepository = usuarioRepository;
        this.driver = driver;
        this.neo4jClient = neo4jClient;
    }

    // Listar todos os usuários
    public Iterable<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // Buscar um usuário pelo ID
    public Optional<Usuario> buscarUsuarioPorId(long id) {
        return usuarioRepository.findById(id);
    }

    // Salvar ou atualizar um usuário
    public Usuario salvarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Deletar um usuário pelo ID
    public void deletarUsuario(long  id) {
        usuarioRepository.deleteById(id);
    }

    // Calcular a idade do usuário
    public int calcularIdade(Usuario usuario) {
        return usuario.getIdade();
    }

    public Usuario cadastrarUsuario(String nome, String senha, String genero, LocalDate dataNascimento) throws IllegalArgumentException {

        Optional<Usuario> usuarioExistente = Optional.ofNullable(usuarioRepository.findByNome(nome));
        if (usuarioExistente.isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário com este nome.");
        }


        Usuario usuario = new Usuario(nome, dataNascimento, genero, senha);
        return usuarioRepository.save(usuario);
    }
    public Long login(String nome, String senha) {
        Usuario usuario = usuarioRepository.findByNomeAndSenha(nome, senha);
        if (usuario == null) {
            throw new IllegalArgumentException("Nome ou senha incorretos.");
        }
        return usuario.getId(); // Retorna o ID do usuário
    }

    public void conectarPlaylistAoUsuario(Long usuarioId, Playlist playlist) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));


        usuario.adicionarPlaylist(playlist);


        usuarioRepository.save(usuario);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public void salvar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }
    public Usuario adicionarMusicaAoUsuario(Long usuarioId, Long musicaId) {

        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(
                () -> new IllegalArgumentException("Usuário não encontrado.")
        );

        Musica musica = musicaRepository.findById(musicaId).orElseThrow(
                () -> new IllegalArgumentException("Música não encontrada.")
        );

        usuario.adicionarMusica(musica);


        return usuarioRepository.save(usuario);
    }
    public void removerMusicaDoUsuario(Long usuarioId, Long musicaId) {
        // Verificar se o usuário existe
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(
                () -> new IllegalArgumentException("Usuário não encontrado.")
        );

        // Verificar se a música existe
        Musica musica = musicaRepository.findById(musicaId).orElseThrow(
                () -> new IllegalArgumentException("Música não encontrada.")
        );

        // Remover o relacionamento no banco
        usuarioRepository.removerRelacionamentoMusica(usuarioId, musicaId);

        // Atualizar a entidade em memória
        usuario.removerMusica(musica);
    }

    private final Driver driver;




    public List<Musica> recomendarMusicasPorArtistas(List<Long> artistasIds) {
        List<Musica> recomendacoes = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = """
        MATCH (a:Artista)<-[:CRIADO_POR]-(m:Musica)
        WHERE id(a) IN $artistasIds
        RETURN DISTINCT m
        ORDER BY rand()  // Ordena os resultados de forma aleatória
        LIMIT 12
        """;

            Result result = session.run(query, Values.parameters("artistasIds", artistasIds));

            result.forEachRemaining(record -> {
                Node node = record.get("m").asNode();
                Musica musica = new Musica();
                musica.setId(node.id());
                musica.setTitulo(node.get("titulo").asString(null));
                musica.setSpotifyId(node.get("spotifyId").asString(null));
                musica.setPreview(node.get("preview").asString(null));
                musica.setLink(node.get("link").asString(null));
                recomendacoes.add(musica);
            });
        }

        return recomendacoes;
    }
    public List<Map<String, Object>> recomendarMusicasPorNomes(List<String> artistasNomes) {
        List<Map<String, Object>> recomendacoes = new ArrayList<>();

        try (Session session = driver.session()) {
            // Consulta Cypher corrigida com WHERE após o UNWIND
            String query = """
        MATCH (a:Artista)<-[:CRIADO_POR]-(m:Musica)
                    WHERE a.nome IN $artistasNomes
                    WITH a, m
                    ORDER BY rand()  // Ordena as músicas aleatoriamente para cada artista
                    WITH a, collect(m)[..3] AS musicasPorArtista  // Pega até 3 músicas aleatórias de cada artista
                    UNWIND musicasPorArtista AS musica
                    RETURN DISTINCT musica, a.nome AS artistaNome
                    LIMIT 15
        """;

            // Executa a consulta e passa os nomes dos artistas como parâmetro
            Result result = session.run(query, Values.parameters("artistasNomes", artistasNomes));

            // Processa os resultados retornados
            result.forEachRemaining(record -> {
                Node node = record.get("musica").asNode();  // Acessando a música
                String artistaNome = record.get("artistaNome").asString();

                if (node != null) {  // Verifica se o nó da música existe
                    Musica musica = new Musica();
                    musica.setId(node.id());
                    musica.setTitulo(node.get("titulo").asString(null));
                    musica.setSpotifyId(node.get("spotifyId").asString(null));
                    musica.setPreview(node.get("preview").asString(null));
                    musica.setLink(node.get("link").asString(null));

                    Map<String, Object> musicaInfo = new HashMap<>();
                    musicaInfo.put("musica", musica);
                    musicaInfo.put("artistaNome", artistaNome);

                    recomendacoes.add(musicaInfo);
                }
            });
        }

        return recomendacoes;
    }

    private final Neo4jClient neo4jClient;


    public List<Map<String, Object>> buscarMusicasAdicionadasPorUsuario(Long userId) {
        List<Map<String, Object>> recomendacoes1 = new ArrayList<>();

        try (Session session1 = driver.session()) {
            // Alteração da consulta para incluir a busca pelo nome do artista
            String query = """
                MATCH (u:Usuario)-[:ADICIONOU]->(m:Musica)-[:CRIADO_POR]->(a:Artista)
                WHERE id(u) = $userId
                RETURN m, a
        """;

            Result result = session1.run(query, Values.parameters("userId", userId));

            result.forEachRemaining(record -> {
                Node nodeMusica = record.get("m").asNode();
                Node nodeArtista = record.get("a").asNode();

                Musica musica = new Musica();
                musica.setId(nodeMusica.id());
                musica.setTitulo(nodeMusica.get("titulo").asString(null));
                musica.setSpotifyId(nodeMusica.get("spotifyId").asString(null));
                musica.setPreview(nodeMusica.get("preview").asString(null));
                musica.setLink(nodeMusica.get("link").asString(null));

                // Obtendo o nome do artista
                String nomeArtista = nodeArtista.get("nome").asString("Artista Desconhecido");

                // Map para retornar ao frontend com a música e o nome do artista
                Map<String, Object> musicaInfo = new HashMap<>();
                musicaInfo.put("musica", musica);         // Adiciona a música
                musicaInfo.put("artistaNome", nomeArtista); // Adiciona o nome do artista

                recomendacoes1.add(musicaInfo);
            });
        }

        return recomendacoes1;
    }



    public List<Map<String, Object>> recomendarMusicasPorUsuarios(Long usuarioId) {
        List<Map<String, Object>> recomendacoes = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = """
                    MATCH (u:Usuario)-[:ADICIONOU]->(m:Musica)<-[:ADICIONOU]-(outro:Usuario)-[:ADICIONOU]->(m2:Musica)
                            MATCH (m2)-[:CRIADO_POR]->(a:Artista)
                            WHERE id(u) = $usuarioId AND NOT (u)-[:ADICIONOU]->(m2)
                            RETURN DISTINCT m2, a.nome AS artistaNome
                            ORDER BY rand()
                            LIMIT 12
        """;

            Result result = session.run(query, Values.parameters("usuarioId", usuarioId));

            result.forEachRemaining(record -> {
                Node node = record.get("m2").asNode();
                String artistaNome = record.get("artistaNome").asString("Artista Desconhecido");

                Musica musica = new Musica();
                musica.setId(node.id());
                musica.setTitulo(node.get("titulo").asString(null));
                musica.setSpotifyId(node.get("spotifyId").asString(null));
                musica.setPreview(node.get("preview").asString(null));
                musica.setLink(node.get("link").asString(null));

                // Map para retornar ao frontend
                Map<String, Object> musicaInfo = new HashMap<>();
                musicaInfo.put("musica", musica);
                musicaInfo.put("artistaNome", artistaNome);

                recomendacoes.add(musicaInfo);
            });
        }

        return recomendacoes;
    }

    public void excluirPlaylistDoUsuario(Long usuarioId, Long playlistId) {
        try (Session session = driver.session()) {
            String query = """
                    MATCH (u:Usuario)-[:CRIAR]->(p:Playlist)
                    WHERE id(u) = $usuarioId AND id(p) = $playlistId
                    DETACH DELETE p
                    """;

            session.run(query, Values.parameters("usuarioId", usuarioId, "playlistId", playlistId));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir a playlist: " + e.getMessage(), e);
        }
    }
    public void removerMusicaDePlaylist(Long usuarioId, Long playlistId, Long musicaId) {
        try (Session session = driver.session()) {
            String query = """
        MATCH (u:Usuario)-[:CRIAR]->(p:Playlist)-[r:Possui]->(m:Musica)
        WHERE id(u) = $usuarioId AND id(p) = $playlistId AND id(m) = $musicaId
        DELETE r
        """;

            session.run(query, Values.parameters(
                    "usuarioId", usuarioId,
                    "playlistId", playlistId,
                    "musicaId", musicaId
            ));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover o relacionamento entre música e playlist: " + e.getMessage(), e);
        }
    }
    public void editarDadosDaPlaylist(Long usuarioId, Long playlistId, String novoNome, String novaDescricao) {
        try (Session session = driver.session()) {
            String query = """
            MATCH (u:Usuario)-[:CRIAR]->(p:Playlist)
            WHERE id(u) = $usuarioId AND id(p) = $playlistId
            SET p.nome = $novoNome, p.descricao = $novaDescricao
            RETURN p
            """;

            session.run(query, Values.parameters(
                    "usuarioId", usuarioId,
                    "playlistId", playlistId,
                    "novoNome", novoNome,
                    "novaDescricao", novaDescricao
            ));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao editar os dados da playlist: " + e.getMessage(), e);
        }
    }



    public void alterarDadosUsuario(Long usuarioId, String nome, String genero, LocalDate dataNascimento, String senha) {
        try (Session session = driver.session()) {
            String query = """
                MATCH (u:Usuario)
                WHERE id(u) = $usuarioId
                SET u.nome = $nome,
                    u.genero = $genero,
                    u.dataNascimento = $dataNascimento,
                    u.senha = $senha
                RETURN u
                """;

            session.run(query, Values.parameters(
                    "usuarioId", usuarioId,
                    "nome", nome,
                    "genero", genero,
                    "dataNascimento", dataNascimento.toString(),
                    "senha", senha
            ));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao alterar os dados do usuário: " + e.getMessage(), e);
        }
    }
    public void excluirUsuario(Long usuarioId) {
        try (Session session = driver.session()) {
            String query = """
            MATCH (u:Usuario)
            WHERE id(u) = $usuarioId
            DETACH DELETE u
            """;

            session.run(query, Values.parameters("usuarioId", usuarioId));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir o usuário: " + e.getMessage(), e);
        }
    }

}
