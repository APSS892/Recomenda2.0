package bcc.ifsuldeminas.sistemaMusicas.model.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;
import java.util.List;


@Node
public class Musica {
    @Id
    @GeneratedValue
    private long id;

    @Property
    private String titulo;

    @Property
    private String spotifyId;

    @Property
    private String preview;

    @Property
    private String link;


    @Relationship(type = "PERTENCE_A", direction = Relationship.Direction.OUTGOING)
    private List<Genero> generos = new ArrayList<>();

    @Relationship(type = "CRIADO_POR", direction = Relationship.Direction.OUTGOING)
    @JsonIgnore
    private List<Artista> artistas = new ArrayList<>();

    public Musica() {}

    public Musica(String titulo, String spotifyId, String preview, String link, List<Genero> generos, List<Artista> artistas) {
        this.titulo = titulo;
        this.spotifyId = spotifyId;
        this.preview = preview;
        this.link = link;
        this.generos = generos;
        this.artistas = artistas;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public List<Genero> getGeneros() {
        return generos;
    }

    public void setGeneros(List<Genero> generos) {
        this.generos = generos;
    }

    public List<Artista> getArtistas() {
        return artistas;
    }

    public void setArtistas(List<Artista> artistas) {
        this.artistas = artistas;
    }

}