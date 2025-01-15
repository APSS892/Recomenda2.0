package bcc.ifsuldeminas.sistemaMusicas.service;

import bcc.ifsuldeminas.sistemaMusicas.model.entities.Artista;
import bcc.ifsuldeminas.sistemaMusicas.model.entities.Genero;
import bcc.ifsuldeminas.sistemaMusicas.model.entities.Musica;
import bcc.ifsuldeminas.sistemaMusicas.repository.ArtistaRepository;
import bcc.ifsuldeminas.sistemaMusicas.repository.GeneroRepository;
import bcc.ifsuldeminas.sistemaMusicas.repository.MusicaRepository;
import jakarta.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
//import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class GeneroService {

    @Autowired
    private GeneroRepository generoRepository;

    public Genero salvarGenero(Genero genero) {
        return generoRepository.save(genero);
    }

    public List<Genero> listarGeneros() {
        return (List<Genero>) generoRepository.findAll();
    }
}
