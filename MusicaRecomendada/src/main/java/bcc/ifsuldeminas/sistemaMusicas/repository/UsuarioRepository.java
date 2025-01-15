package bcc.ifsuldeminas.sistemaMusicas.repository;

import bcc.ifsuldeminas.sistemaMusicas.model.entities.Musica;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import bcc.ifsuldeminas.sistemaMusicas.model.entities.Usuario;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends Neo4jRepository<Usuario, Long> {
    Optional<Usuario> findById(long id);


    void deleteById(long id);
    @Query("MATCH (u:Usuario) WHERE u.nome = $nome RETURN u")
    Usuario findByNome(String nome);

    @Query("MATCH (u:Usuario)-[r:ADICIONOU]->(m:Musica) WHERE id(u) = $usuarioId AND id(m) = $musicaId DELETE r")
    void removerRelacionamentoMusica(Long usuarioId, Long musicaId);
    Usuario findByNomeAndSenha(String nome, String senha);


}