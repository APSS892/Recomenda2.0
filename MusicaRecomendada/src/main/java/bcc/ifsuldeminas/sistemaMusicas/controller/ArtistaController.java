package bcc.ifsuldeminas.sistemaMusicas.controller;


import bcc.ifsuldeminas.sistemaMusicas.model.entities.Artista;
import bcc.ifsuldeminas.sistemaMusicas.repository.ArtistaRepository;
import bcc.ifsuldeminas.sistemaMusicas.service.ArtistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private ArtistaRepository artistaRepository;

    @PostMapping
    public Artista criarArtista(@RequestBody Artista artista) {
        return artistaService.salvarArtista(artista);
    }

    @GetMapping
    public List<Artista> listarArtistas() {
        return artistaService.listarArtistas();
    }

    @GetMapping("/artistasapi")
    public ResponseEntity<Map<String, Object>> listarArtistasPaginados(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "30") int size) {

        Map<String, Object> artistas = artistaService.listarArtistasPaginados(page, size);
        return ResponseEntity.ok(artistas);
    }

    @GetMapping("/artistasapi/genero")
    public ResponseEntity<Map<String, Object>> listarArtistasPorGenero(
            @RequestParam(value = "genero") String genero,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "30") int size) {

        Map<String, Object> artistas = artistaService.listarArtistasPorGenero(genero, page, size);
        return ResponseEntity.ok(artistas);
    }

    @GetMapping("/artistasapi/nome")
    public ResponseEntity<Map<String, Object>> listarArtistasPorNome(
            @RequestParam(value = "nome") String nome,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "30") int size) {

        Map<String, Object> artistas = artistaService.listarArtistasPorNome(nome, page, size);
        return ResponseEntity.ok(artistas);
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
}