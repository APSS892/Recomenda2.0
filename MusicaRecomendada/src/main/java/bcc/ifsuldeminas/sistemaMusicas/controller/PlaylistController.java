package bcc.ifsuldeminas.sistemaMusicas.controller;

import bcc.ifsuldeminas.sistemaMusicas.model.entities.Playlist;
import bcc.ifsuldeminas.sistemaMusicas.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    @Autowired
    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping
    public ResponseEntity<Iterable<Playlist>> listarPlaylists() {
        Iterable<Playlist> playlists = playlistService.listarPlaylists();
        return new ResponseEntity<>(playlists, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Playlist> buscarPlaylistPorId(@PathVariable long id) {
        Optional<Playlist> playlist = playlistService.buscarPlaylistPorId(id);
        return playlist.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Playlist> salvarPlaylist(@RequestBody Playlist playlist) {
        Playlist playlistSalva = playlistService.salvarPlaylist(playlist);
        return new ResponseEntity<>(playlistSalva, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPlaylist(@PathVariable long id) {
        Optional<Playlist> playlist = playlistService.buscarPlaylistPorId(id);
        if (playlist.isPresent()) {
            playlistService.deletarPlaylist(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/criar")
    public ResponseEntity<Playlist> criarPlaylist(@RequestParam String nome, @RequestParam String descricao) {
        Playlist playlist = playlistService.criarPlaylist(nome, descricao);
        return ResponseEntity.ok(playlist);
    }

    @PostMapping("/{playlistId}/adicionarMusica/{musicaId}")
    public ResponseEntity<Playlist> adicionarMusicaNaPlaylist(@PathVariable Long playlistId, @PathVariable Long musicaId) {
        Playlist playlist = playlistService.adicionarMusicaNaPlaylist(playlistId, musicaId);
        return ResponseEntity.ok(playlist);
    }

}
