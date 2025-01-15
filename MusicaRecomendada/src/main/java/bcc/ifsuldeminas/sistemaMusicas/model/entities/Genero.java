package bcc.ifsuldeminas.sistemaMusicas.model.entities;

import org.springframework.data.neo4j.core.schema.*;
import java.util.ArrayList;
import java.util.List;

@Node
public class Genero {
    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String nome;

    @Property
    private String spotifyId;

    @Relationship(type = "PERTENCE_A", direction = Relationship.Direction.OUTGOING)
    private List<Musica> musicas = new ArrayList<>();

    @Relationship(type = "ESTILO", direction = Relationship.Direction.OUTGOING)
    private List<Artista> artistas = new ArrayList<>(); // Artistas relacionados a esse Gênero

    public Genero() {}

    public Genero(String nome, String spotifyId) {
        this.nome = nome;
        this.spotifyId = spotifyId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public List<Musica> getMusicas() {
        return musicas;
    }

    public void setMusicas(List<Musica> musicas) {
        this.musicas = musicas;
    }

    public List<Artista> getArtistas() {
        return artistas;
    }

    public void setArtistas(List<Artista> artistas) {
        this.artistas = artistas;
    }
}
