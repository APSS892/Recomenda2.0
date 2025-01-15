package bcc.ifsuldeminas.sistemaMusicas.model.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node
public class Artista {
    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String nome;

    @Property
    private String spotifyId;

    @Property
    private String link;

    @Property
    private String picture;

    @Relationship(type = "CRIADO_POR", direction = Relationship.Direction.INCOMING)
    private List<Musica> musicas = new ArrayList<>();

    @Relationship(type = "ESTILO", direction = Relationship.Direction.OUTGOING)
    private List<Genero> generos = new ArrayList<>();

    // Lista para armazenar os IDs dos álbuns
    @Property
    private List<String> albumIds = new ArrayList<>();  // Novo campo

    public Artista() {}

    public Artista(String nome, String spotifyId, String link, String picture) {
        this.nome = nome;
        this.spotifyId = spotifyId;
        this.picture = picture;
        this.link = link;
    }

    // Getters e setters

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<Musica> getMusicas() {
        return musicas;
    }

    public void setMusicas(List<Musica> musicas) {
        this.musicas = musicas;
    }

    public List<Genero> getGeneros() {
        return generos;
    }

    public void setGeneros(List<Genero> generos) {
        this.generos = generos;
    }

    public List<String> getAlbumIds() {
        return albumIds;
    }

    public void setAlbumIds(List<String> albumIds) {
        this.albumIds = albumIds;
    }


    public void adicionarGenero(Genero genero) {
        if (!this.generos.contains(genero)) {
            this.generos.add(genero);
        }
    }


    public void adicionarAlbumId(String albumId) {
        if (!this.albumIds.contains(albumId)) {
            this.albumIds.add(albumId);
        }
    }
}

