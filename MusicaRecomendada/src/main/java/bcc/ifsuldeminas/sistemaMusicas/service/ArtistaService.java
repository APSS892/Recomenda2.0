package bcc.ifsuldeminas.sistemaMusicas.service;

import bcc.ifsuldeminas.sistemaMusicas.model.entities.Artista;
import bcc.ifsuldeminas.sistemaMusicas.repository.ArtistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class ArtistaService {

    @Autowired
    private ArtistaRepository artistaRepository;

    public Artista salvarArtista(Artista artista) {
        return artistaRepository.save(artista);
    }

    public List<Artista> listarArtistas() {
        return (List<Artista>) artistaRepository.findAll();
    }
    /*public List<Artista> buscarArtistasPorGenero(String genero) {
        return artistaRepository.findByGeneroIgnoreCase(genero);
    }*/
}