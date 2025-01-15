package bcc.ifsuldeminas.sistemaMusicas.repository;

import bcc.ifsuldeminas.sistemaMusicas.model.entities.Genero;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeneroRepository extends Neo4jRepository<Genero, Long> {
    Optional<Genero> findByNome(String generoNome);

    Genero findBySpotifyId(String id);
}
