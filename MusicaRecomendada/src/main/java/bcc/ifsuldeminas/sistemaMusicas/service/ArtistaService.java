package bcc.ifsuldeminas.sistemaMusicas.service;

import bcc.ifsuldeminas.sistemaMusicas.model.entities.Artista;
import bcc.ifsuldeminas.sistemaMusicas.repository.ArtistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.neo4j.core.Neo4jClient;
import java.util.List;
import java.util.Map;
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

   /* public List<Artista> listarTodos() {
        return artistaRepository.findAll();
    }

    public List<Artista> listarPorGenero(String genero) {
        return artistaRepository.findByGenero(genero);
    }*/

    @Autowired
    private Neo4jClient neo4jClient;

    public List<Map<String, Object>> listarTodosArtistas() {
        String query = """
            MATCH (a:Artista)-[:ESTILO]->(g:Genero)
                    OPTIONAL MATCH (a)-[:CRIADO_POR]->(m:Musica)
                    WITH a, collect(g { .id, .nome, .spotifyId }) AS generos, collect(m { .id, .titulo, .spotifyId }) AS musicas
                    RETURN a { .id, .nome, .spotifyId, .link, .picture, generos: generos, musicas: musicas } AS artista
                
        """;

        return (List<Map<String, Object>>) neo4jClient.query(query)
                .fetch()
                .all();
    }

    public List<Map<String, Object>> buscarArtistaPorNome(String nome) {
        String query = """
            MATCH (a:Artista)-[:ESTILO]->(g:Genero)
            OPTIONAL MATCH (a)-[:CRIADO_POR]->(m:Musica)
            WHERE toLower(a.nome) CONTAINS toLower($nome)
            RETURN a { .id, .nome, .spotifyId, .link, .picture, 
                       generos: collect(g { .id, .nome, .spotifyId }), 
                       musicas: collect(m { .id, .titulo, .spotifyId }) } AS artista
        """;

        return (List<Map<String, Object>>) neo4jClient.query(query)
                .bind(nome)
                .to("nome")
                .fetch()
                .all();
    }


    public List<Map<String, Object>> listarGeneros() {
        String query = """
            MATCH (g:Genero)
            RETURN g { .id, .nome, .spotifyId } AS genero
        """;

        return (List<Map<String, Object>>) neo4jClient.query(query)
                .fetch()
                .all();
    }

    public List<Map<String, Object>> buscarMusicasPorArtista(Long artistaId) {
        String query = """
            MATCH (a:Artista)-[:CRIADO_POR]->(m:Musica)
            WHERE id(a) = $artistaId
            RETURN m { .id, .titulo, .spotifyId, .preview, .link } AS musica
        """;

        return (List<Map<String, Object>>) neo4jClient.query(query)
                .bind(artistaId)
                .to("artistaId")
                .fetch()
                .all();
    }
}