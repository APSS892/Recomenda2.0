package bcc.ifsuldeminas.sistemaMusicas.service;

import bcc.ifsuldeminas.sistemaMusicas.model.entities.Artista;
import bcc.ifsuldeminas.sistemaMusicas.repository.ArtistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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

    @Autowired
    private Neo4jClient neo4jClient;

    public Map<String, Object> listarArtistasPaginados(int page, int size) {
        // Calcula o número de elementos a serem pulados e limitados
        int skip = page * size;

        // Consulta Cypher para artistas com paginação
        String query = """
                    MATCH (a:Artista)-[:ESTILO]->(g:Genero)
                            OPTIONAL MATCH (a)-[:CRIADO_POR]->(m:Musica)
                            WITH a, collect(g { .id, .nome, .spotifyId }) AS generos, collect(m { .id, .titulo, .spotifyId }) AS musicas
                            RETURN a { .id, .nome, .spotifyId, .link, .picture, generos: generos, musicas: musicas } AS artista
                            SKIP $skip
                            LIMIT $size
                """;

        // Executa a consulta com os parâmetros de paginação
        List<Map<String, Object>> artistas = (List<Map<String, Object>>) neo4jClient.query(query)
                .bind(skip).to("skip")  // Ajustando para usar o nome do parâmetro como string
                .bind(size).to("size")  // Ajustando para usar o nome do parâmetro como string
                .fetch()
                .all();

        // Consulta para contar o número total de artistas
        String countQuery = """
                    MATCH (a:Artista)-[:ESTILO]->(g:Genero)
                            OPTIONAL MATCH (a)-[:CRIADO_POR]->(m:Musica)
                            RETURN count(a) AS total
                """;

        Optional<Map<String, Object>> result = neo4jClient.query(countQuery)
                .fetch()
                .one();

        long total = 0; // Inicialize a variável

        if (result.isPresent()) {
            total = (Long) result.get().get("total"); // Acessando o valor de "total" corretamente
        } else {
            // Caso não tenha retornado nenhum resultado
            total = 0; // Ou qualquer valor padrão que faça sentido
        }

// Agora você pode calcular o total de páginas
        int totalPages = (int) Math.ceil((double) total / size);

// Retornando os dados no mapa
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("artistas", artistas); // Isso é assumido, deve ser a lista de artistas
        resultMap.put("total", total); // Coloca o total calculado
        resultMap.put("totalPages", totalPages); // Coloca o total de páginas
        resultMap.put("currentPage", page); // Coloca a página atual

        return resultMap;
    }

    public Map<String, Object> listarArtistasPorGenero(String genero, int page, int size) {
        int skip = page * size;

        // Consulta Cypher filtrando por gênero
        String query = """
        MATCH (a:Artista)-[:ESTILO]->(g:Genero)
        WHERE g.nome = $genero
        OPTIONAL MATCH (a)-[:CRIADO_POR]->(m:Musica)
        WITH a, collect(g { .id, .nome, .spotifyId }) AS generos, collect(m { .id, .titulo, .spotifyId }) AS musicas
        RETURN a { .id, .nome, .spotifyId, .link, .picture, generos: generos, musicas: musicas } AS artista
        SKIP $skip
        LIMIT $size
    """;

        List<Map<String, Object>> artistas = (List<Map<String, Object>>) neo4jClient.query(query)
                .bind(genero).to("genero")
                .bind(skip).to("skip")
                .bind(size).to("size")
                .fetch()
                .all();

        // Contar o total de artistas com esse gênero
        String countQuery = """
        MATCH (a:Artista)-[:ESTILO]->(g:Genero)
        WHERE g.nome = $genero
        RETURN count(a) AS total
    """;

        Optional<Map<String, Object>> result = neo4jClient.query(countQuery)
                .bind(genero).to("genero")
                .fetch()
                .one();

        long total = result.map(r -> (Long) r.get("total")).orElse(0L);
        int totalPages = (int) Math.ceil((double) total / size);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("artistas", artistas);
        resultMap.put("total", total);
        resultMap.put("totalPages", totalPages);
        resultMap.put("currentPage", page);

        return resultMap;
    }

    public Map<String, Object> listarArtistasPorNome(String nome, int page, int size) {
        int skip = page * size;

        // Consulta Cypher para buscar artistas por nome com paginação
        String query = """
                MATCH (a:Artista)-[:ESTILO]->(g:Genero)
                WHERE toLower(a.nome) CONTAINS toLower($nome)
                OPTIONAL MATCH (a)-[:CRIADO_POR]->(m:Musica)
                WITH a, collect(g { .id, .nome, .spotifyId }) AS generos, collect(m { .id, .titulo, .spotifyId }) AS musicas
                RETURN a { .id, .nome, .spotifyId, .link, .picture, generos: generos, musicas: musicas } AS artista
                SKIP $skip
                LIMIT $size
        """;

        // Executa a consulta Cypher com o nome e parâmetros de paginação
        List<Map<String, Object>> artistas = (List<Map<String, Object>>) neo4jClient.query(query)
                .bind(nome).to("nome")
                .bind(skip).to("skip")
                .bind(size).to("size")
                .fetch()
                .all();

        // Consulta para contar o total de artistas com esse nome
        String countQuery = """
                MATCH (a:Artista)-[:ESTILO]->(g:Genero)
                WHERE toLower(a.nome) CONTAINS toLower($nome)
                RETURN count(a) AS total
        """;

        Optional<Map<String, Object>> result = neo4jClient.query(countQuery)
                .bind(nome).to("nome")
                .fetch()
                .one();

        long total = result.map(r -> (Long) r.get("total")).orElse(0L);
        int totalPages = (int) Math.ceil((double) total / size);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("artistas", artistas);
        resultMap.put("total", total);
        resultMap.put("totalPages", totalPages);
        resultMap.put("currentPage", page);

        return resultMap;
    }

}