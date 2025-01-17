package bcc.ifsuldeminas.sistemaMusicas.controller;

import bcc.ifsuldeminas.sistemaMusicas.model.entities.Musica;
import bcc.ifsuldeminas.sistemaMusicas.model.entities.Playlist;
import bcc.ifsuldeminas.sistemaMusicas.model.entities.Usuario;
import bcc.ifsuldeminas.sistemaMusicas.service.DeezerTrack;
import bcc.ifsuldeminas.sistemaMusicas.service.DeezerTracksResponse;
import bcc.ifsuldeminas.sistemaMusicas.service.PlaylistService;
import bcc.ifsuldeminas.sistemaMusicas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;


    @Autowired
    private PlaylistService playlistService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService,PlaylistService playlistService) {

        this.usuarioService = usuarioService;
        this.playlistService = playlistService;
    }
    // Endpoint para listar todos os usuários
    @GetMapping
    public ResponseEntity<Iterable<Usuario>> listarUsuarios() {
        Iterable<Usuario> usuarios = usuarioService.listarUsuarios();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    // Endpoint para buscar um usuário pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable long  id) {
        Optional<Usuario> usuario = usuarioService.buscarUsuarioPorId(id);
        if (usuario.isPresent()) {
            return new ResponseEntity<>(usuario.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint para salvar ou atualizar um usuário
    @PostMapping
    public ResponseEntity<Usuario> salvarUsuario(@RequestBody Usuario usuario) {
        Usuario usuarioSalvo = usuarioService.salvarUsuario(usuario);
        return new ResponseEntity<>(usuarioSalvo, HttpStatus.CREATED);
    }

    // Endpoint para deletar um usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable long  id) {
        Optional<Usuario> usuario = usuarioService.buscarUsuarioPorId(id);
        if (usuario.isPresent()) {
            usuarioService.deletarUsuario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint para calcular a idade de um usuário
    @GetMapping("/{id}/idade")
    public ResponseEntity<Integer> calcularIdade(@PathVariable long id) {
        Optional<Usuario> usuario = usuarioService.buscarUsuarioPorId(id);
        if (usuario.isPresent()) {
            int idade = usuarioService.calcularIdade(usuario.get());
            return new ResponseEntity<>(idade, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/cadastrar")
    public ResponseEntity<String> cadastrarUsuario(
            @RequestParam String nome,
            @RequestParam String senha,
            @RequestParam String genero,
            @RequestParam String dataNascimento) {

        try {
            LocalDate data = LocalDate.parse(dataNascimento);
            usuarioService.cadastrarUsuario(nome, senha, genero, data);
            return ResponseEntity.ok("Usuário cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("Erro no cadastro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno no servidor.");
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String nome, @RequestParam String senha) {
        try {
            Long usuarioId = usuarioService.login(nome, senha);
            if (usuarioId != null) {
                return ResponseEntity.ok(usuarioId); // Retorna o ID do usuário
            } else {
                return ResponseEntity.status(400).body("Login falhou.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno no servidor.");
        }
    }

    @PostMapping("/criar")
    public ResponseEntity<Playlist> criarPlaylist(@RequestParam String nome, @RequestParam String descricao) {
        Playlist playlist = playlistService.criarPlaylist(nome, descricao);
        return ResponseEntity.ok(playlist);
    }
    @PostMapping("/criarplaylist")
    public ResponseEntity<?> criarPlaylist(
            @RequestParam Long id,
            @RequestParam String nome,
            @RequestParam String descricao) {
        try {

            Playlist playlist = playlistService.criarPlaylist(nome, descricao);


            usuarioService.conectarPlaylistAoUsuario(id, playlist);

            return ResponseEntity.ok(playlist);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar a playlist e conectar ao usuário.");
        }
    }

    @PostMapping("/{playlistId}/adicionarMusica/{musicaId}")
    public ResponseEntity<Playlist> adicionarMusicaNaPlaylist(@PathVariable Long playlistId, @PathVariable Long musicaId) {
        Playlist playlist = playlistService.adicionarMusicaNaPlaylist(playlistId, musicaId);
        return ResponseEntity.ok(playlist);
    }
    @PostMapping("/adiciona/adicionarMusica")
    public ResponseEntity<?> adicionarMusicaNaPlaylist(
            @RequestParam Long id,
            @RequestParam Long playlistId,
            @RequestParam Long musicaId) {
        try {
              Playlist playlist = playlistService.adicionarMusicaNaPlaylistDoUsuario(id, playlistId, musicaId);
            return ResponseEntity.ok(playlist);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao adicionar música na playlist.");
        }
    }
    @PostMapping("/gostaMusica")
    public ResponseEntity<?> adicionarMusicaAoUsuario(
            @RequestParam Long id,
            @RequestParam Long musicaId) {
        try {
            Usuario usuario = usuarioService.adicionarMusicaAoUsuario(id, musicaId);
            return ResponseEntity.ok(usuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao adicionar música ao usuário.");
        }
    }
    @GetMapping("/musicasAdicionadas")
    public ResponseEntity<List<Musica>> buscarMusicasAdicionadas(@RequestParam Long id) {
        List<Musica> musicas = usuarioService.buscarMusicasAdicionadasPorUsuario(id);
        return ResponseEntity.ok(musicas);
    }
    @DeleteMapping("/removerMusica")
    public ResponseEntity<?> removerMusicaDoUsuario(
            @RequestParam Long id,
            @RequestParam Long musicaId) {
        try {
            usuarioService.removerMusicaDoUsuario(id, musicaId);
            return ResponseEntity.ok("Música removida com sucesso do usuário.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao remover música do usuário.");
        }
    }/*
    @GetMapping("/recomendacoes/musicas")
    public ResponseEntity<?> recomendarMusicasPorArtistas(@RequestParam List<Long> artistasIds) {
        List<Musica> recomendacoes = usuarioService.recomendarMusicasPorArtistas(artistasIds);
        return ResponseEntity.ok(recomendacoes);
    }*/

    @GetMapping("/recomendacoes/usuarios")
    public ResponseEntity<?> recomendarMusicasPorUsuarios(@RequestParam Long id) {
        List<Musica> recomendacoes = usuarioService.recomendarMusicasPorUsuarios(id);
        return ResponseEntity.ok(recomendacoes);
    }
    @GetMapping("/recomendacoes/musica")
    public ResponseEntity<?> recomendarMusicasPorArtistas(@RequestParam List<String> nome) {
        if (nome == null || nome.isEmpty()) {
            return ResponseEntity.badRequest().body("Nenhum nome de artista foi fornecido.");
        }

        List<Musica> recomendacoes = usuarioService.recomendarMusicasPorNomes(nome);

        if (recomendacoes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma música encontrada para os artistas fornecidos.");
        }

        return ResponseEntity.ok(recomendacoes);
    }

    @DeleteMapping("/{usuarioId}/playlists/{playlistId}")
    public ResponseEntity<?> excluirPlaylistDoUsuario(
            @PathVariable Long usuarioId,
            @PathVariable Long playlistId) {
        try {
            usuarioService.excluirPlaylistDoUsuario(usuarioId, playlistId);
            return ResponseEntity.ok("Playlist excluída com sucesso.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao excluir a playlist: " + e.getMessage());
        }
    }
    @PutMapping("/editar/{usuarioId}")
    public ResponseEntity<?> alterarDadosUsuario(
            @PathVariable Long usuarioId,
            @RequestBody Map<String, Object> dadosUsuario) {
        try {
            String nome = (String) dadosUsuario.get("nome");
            String genero = (String) dadosUsuario.get("genero");
            String senha = (String) dadosUsuario.get("senha");
            LocalDate dataNascimento = LocalDate.parse((String) dadosUsuario.get("dataNascimento"));

            usuarioService.alterarDadosUsuario(usuarioId, nome, genero, dataNascimento, senha);
            return ResponseEntity.ok("Dados do usuário alterados com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao alterar os dados do usuário: " + e.getMessage());
        }
    }
    @DeleteMapping("/{usuarioId}/playlists/{playlistId}/musicas/{musicaId}")
    public ResponseEntity<?> removerMusicaDePlaylist(
            @PathVariable Long usuarioId,
            @PathVariable Long playlistId,
            @PathVariable Long musicaId) {
        try {
            usuarioService.removerMusicaDePlaylist(usuarioId, playlistId, musicaId);
            return ResponseEntity.ok("Música removida da playlist com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao remover música da playlist: " + e.getMessage());
        }
    }
    @PutMapping("/{usuarioId}/playlists/{playlistId}")
    public ResponseEntity<?> editarDadosDaPlaylist(
            @PathVariable Long usuarioId,
            @PathVariable Long playlistId,
            @RequestBody Map<String, String> dadosAtualizados) {
        try {
            String novoNome = dadosAtualizados.get("nome");
            String novaDescricao = dadosAtualizados.get("descricao");

            usuarioService.editarDadosDaPlaylist(usuarioId, playlistId, novoNome, novaDescricao);
            return ResponseEntity.ok("Dados da playlist atualizados com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao editar os dados da playlist: " + e.getMessage());
        }
    }

    @DeleteMapping("/excluir/{usuarioId}")
    public ResponseEntity<?> excluirUsuario(@PathVariable Long usuarioId) {
        try {
            usuarioService.excluirUsuario(usuarioId);
            return ResponseEntity.ok("Usuário excluído com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao excluir o usuário: " + e.getMessage());
        }
    }

}



