package bcc.ifsuldeminas.sistemaMusicas.service;

import bcc.ifsuldeminas.sistemaMusicas.model.entities.Musica;
import bcc.ifsuldeminas.sistemaMusicas.model.entities.Playlist;
import bcc.ifsuldeminas.sistemaMusicas.model.entities.Usuario;
import bcc.ifsuldeminas.sistemaMusicas.repository.MusicaRepository;
import bcc.ifsuldeminas.sistemaMusicas.repository.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private MusicaRepository musicaRepository;

    @Autowired
    public PlaylistService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public Playlist salvarPlaylist(Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    public Optional<Playlist> buscarPlaylistPorId(long id) {
        return playlistRepository.findById(id);
    }

    public Iterable<Playlist> listarPlaylists() {
        return playlistRepository.findAll();
    }

    public void deletarPlaylist(long id) {
        playlistRepository.deleteById(id);
    }

    public Playlist criarPlaylist(String nome, String descricao) {
        Playlist playlist = new Playlist(nome, descricao);
        return playlistRepository.save(playlist);
    }

    public Playlist adicionarMusicaNaPlaylist(Long playlistId, Long musicaId) {
        Optional<Playlist> playlistOpt = playlistRepository.findById(playlistId);
        Optional<Musica> musicaOpt = musicaRepository.findById(musicaId);

        if (playlistOpt.isPresent() && musicaOpt.isPresent()) {
            Playlist playlist = playlistOpt.get();
            Musica musica = musicaOpt.get();

            playlist.getMusicas().add(musica);
            return playlistRepository.save(playlist);
        }

        throw new RuntimeException("Desculpe, não conseguimos encontrar.");
    }
    public Playlist criarPlaylistParaUsuario(Long usuarioId, String nome, String descricao) {

        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }


        Playlist playlist = new Playlist(nome, descricao);
        playlist = playlistRepository.save(playlist);


        usuario.adicionarPlaylist(playlist);
        usuarioService.salvar(usuario);

        return playlist;
    }/*
    public Playlist adicionarMusicaNaPlaylistDoUsuario(Long usuarioId, Long playlistId, Long musicaId) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        Playlist playlist = playlistRepository.findById(playlistId).orElse(null);
        if (playlist == null || usuario.getPlaylists().stream().noneMatch(p -> p.getId() == playlistId)) {
            throw new IllegalArgumentException("Playlist não encontrada ou não pertence ao usuário.");
        }

        Musica musica = musicaRepository.findById(musicaId).orElse(null);
        if (musica == null) {
            throw new IllegalArgumentException("Música não encontrada.");
        }

        playlist.adicionarMusica(musica);
        playlistRepository.save(playlist);

        usuario.adicionarMusica(musica);
        usuarioService.salvar(usuario);

        return playlist;
    }*/
    public Playlist adicionarMusicaNaPlaylistDoUsuario(Long usuarioId, Long playlistId, Long musicaId) {
        // Verificando se o usuário existe
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        // Buscando a playlist
        Playlist playlist = playlistRepository.findById(playlistId).orElse(null);
        if (playlist == null || usuario.getPlaylists().stream().noneMatch(p -> p.getId() == playlistId)) {
            throw new IllegalArgumentException("Playlist não encontrada ou não pertence ao usuário.");
        }

        // Buscando a música
        Musica musica = musicaRepository.findById(musicaId).orElse(null);
        if (musica == null) {
            throw new IllegalArgumentException("Música não encontrada.");
        }



        // Adicionando a música à playlist
        //playlist.adicionarMusica(musica);

        // Salvando a playlist com o novo relacionamento
        //playlistRepository.save(playlist);

        // Adicionando a música ao usuário
        usuario.adicionarMusica(musica);
        usuarioService.salvar(usuario);

        Optional<Playlist> playlistOpt = playlistRepository.findById(playlistId);
        Optional<Musica> musicaOpt = musicaRepository.findById(musicaId);

        if (playlistOpt.isPresent() && musicaOpt.isPresent()) {
            playlist = playlistOpt.get();
            musica = musicaOpt.get();

            playlist.getMusicas().add(musica);
            return playlistRepository.save(playlist);
        }

        return playlist;
    }


/*
    public Playlist adicionarMusicaNaPlaylistDoUsuario(Long usuarioId, Long playlistId, Long musicaId) {

        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }


        Playlist playlist = playlistRepository.findById(playlistId).orElse(null);
        if (playlist == null || !usuario.getPlaylists().contains(playlist.getId()==playlistId)) {
            throw new IllegalArgumentException("Playlist não encontrada ou não pertence ao usuário.");
        }


        Musica musica = musicaRepository.findById(musicaId).orElse(null);
        if (musica == null) {
            throw new IllegalArgumentException("Música não encontrada.");
        }


        playlist.getMusicas().add(musica);
        playlistRepository.save(playlist);

        usuario.adicionarMusica(musica);
        usuarioService.salvar(usuario);

        return playlist;
    }*/
}
