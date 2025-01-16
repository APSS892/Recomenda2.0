package bcc.ifsuldeminas.sistemaMusicas.controller;

import bcc.ifsuldeminas.sistemaMusicas.model.entities.Genero;
import bcc.ifsuldeminas.sistemaMusicas.model.entities.Musica;
import bcc.ifsuldeminas.sistemaMusicas.repository.GeneroRepository;
import bcc.ifsuldeminas.sistemaMusicas.service.GeneroService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/generos")
public class GeneroController {

    @Autowired
    private GeneroService generoService;

    @Autowired
    private GeneroRepository generoRepository;

    @PostMapping
    public Genero criarGenero(@RequestBody Genero genero) {
        return generoService.salvarGenero(genero);
    }

    @GetMapping
    public List<Genero> listarGeneros() {
        return generoService.listarGeneros();
    }

    @GetMapping("/generosapi")
    public List<Genero> getGeneros() {
        return generoRepository.findAll();
    }
}