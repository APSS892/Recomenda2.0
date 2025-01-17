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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        LIMIT 10
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
    public List<Musica> recomendarMusicasPorNomes(List<String> artistasNomes) {
        List<Musica> recomendacoes = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = """
        MATCH (a:Artista)<-[:CRIADO_POR]-(m:Musica)
        WHERE a.nome IN $artistasNomes
        RETURN DISTINCT m
        ORDER BY rand()  // Ordena os resultados de forma aleatória
        LIMIT 10
        """;

            Result result = session.run(query, Values.parameters("artistasNomes", artistasNomes));

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
    private final Neo4jClient neo4jClient;

    public List<Musica> buscarMusicasAdicionadasPorUsuario(Long userId) {

        List<Musica> recomendacoes1 = new ArrayList<>();

        try (Session session1 = driver.session()) {
            String query = """
        MATCH (u:Usuario)-[:ADICIONOU]->(m:Musica)
        WHERE id(u) = $userId
        RETURN m
        """;

            Result result = session1.run(query, Values.parameters("userId", userId));

            result.forEachRemaining(record -> {
                Node node = record.get("m").asNode();
                Musica musica = new Musica();
                musica.setId(node.id());
                musica.setTitulo(node.get("titulo").asString(null));
                musica.setSpotifyId(node.get("spotifyId").asString(null));
                musica.setPreview(node.get("preview").asString(null));
                musica.setLink(node.get("link").asString(null));
                recomendacoes1.add(musica);
            });
        }

        return recomendacoes1;
    }



    public List<Musica> recomendarMusicasPorUsuarios(Long usuarioId) {
        List<Musica> recomendacoes = new ArrayList<>();

        try (Session session = driver.session()) {
            String query = """
            MATCH (u:Usuario)-[:ADICIONOU]->(m:Musica)<-[:ADICIONOU]-(outro:Usuario)-[:ADICIONOU]->(m2:Musica)
            WHERE id(u) = $usuarioId AND NOT (u)-[:ADICIONOU]->(m2)
            RETURN DISTINCT m2
            ORDER BY rand()
            LIMIT 10
            """;

            Result result = session.run(query, Values.parameters("usuarioId", usuarioId));

            result.forEachRemaining(record -> {

                Node node = record.get("m2").asNode();
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
