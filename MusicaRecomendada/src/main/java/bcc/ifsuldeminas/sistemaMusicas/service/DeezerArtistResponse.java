package bcc.ifsuldeminas.sistemaMusicas.service;

import java.util.List;

public class DeezerArtistResponse {
    private String id;
    private String name;
    private String link;
    private String picture; // Adicionado o campo picture
    private Genres genres;

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getPicture() { return picture; } // Getter para picture
    public void setPicture(String picture) { this.picture = picture; } // Setter para picture

    public Genres getGenres() { return genres; }
    public void setGenres(Genres genres) { this.genres = genres; }

    // Classe interna Genres
    public static class Genres {
        private List<DeezerGenre> data;

        public List<DeezerGenre> getData() { return data; }
        public void setData(List<DeezerGenre> data) { this.data = data; }
    }
}
