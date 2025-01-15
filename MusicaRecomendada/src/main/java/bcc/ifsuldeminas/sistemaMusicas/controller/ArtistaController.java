package bcc.ifsuldeminas.sistemaMusicas.controller;


import bcc.ifsuldeminas.sistemaMusicas.model.entities.Artista;
import bcc.ifsuldeminas.sistemaMusicas.service.ArtistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/artistas")
public class ArtistaController {

    @Autowired
    private ArtistaService artistaService;

    @PostMapping
    public Artista criarArtista(@RequestBody Artista artista) {
        return artistaService.salvarArtista(artista);
    }

    @GetMapping("/listar")
    public List<Artista> listarArtistas() {
        return artistaService.listarArtistas();
    }

    /*@GetMapping("/por-genero")
    public ResponseEntity<List<Artista>> buscarArtistasPorGenero(@RequestParam String genero) {
        try {
            List<Artista> artistas = artistaService.buscarArtistasPorGenero(genero);
            if (artistas.isEmpty()) {
                return ResponseEntity.status(404).body(artistas); // Nenhum artista encontrado
            }
            return ResponseEntity.ok(artistas); // Lista de artistas
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Erro interno do servidor
        }
    }*//*
    @GetMapping
    public ResponseEntity<List<Artista>> listarArtistas() {
        List<Artista> artistas = artistaService.listarTodos();
        List<Artista> artistaDTOs = artistas.stream()
                .map(artista -> new Artista(artista.getNome(), artista.getFotoUrl()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(artistaDTOs);
    }*/

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarTodosArtistas() {
        List<Map<String, Object>> artistas = artistaService.listarTodosArtistas();
        return ResponseEntity.ok(artistas);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Map<String, Object>>> buscarArtistaPorNome(@RequestParam String nome) {
        List<Map<String, Object>> artistas = artistaService.buscarArtistaPorNome(nome);
        return ResponseEntity.ok(artistas);
    }

    @GetMapping("/generos")
    public ResponseEntity<List<Map<String, Object>>> listarGeneros() {
        List<Map<String, Object>> generos = artistaService.listarGeneros();
        return ResponseEntity.ok(generos);
    }

    @GetMapping("/{artistaId}/musicas")
    public ResponseEntity<List<Map<String, Object>>> buscarMusicasPorArtista(@PathVariable Long artistaId) {
        List<Map<String, Object>> musicas = artistaService.buscarMusicasPorArtista(artistaId);
        return ResponseEntity.ok(musicas);
    }
}