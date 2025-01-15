package bcc.ifsuldeminas.sistemaMusicas.service;

import java.util.List;

public class DeezerAlbumResponse {
    private Long id;
    private String title;
    private Artist artist;
    private Genre genres;
    private TrackList tracks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Genre getGenres() {
        return genres;
    }

    public void setGenres(Genre genres) {
        this.genres = genres;
    }

    public TrackList getTracks() {
        return tracks;
    }

    public void setTracks(TrackList tracks) {
        this.tracks = tracks;
    }

    public static class Artist {
        private Long id;
        private String name;
        private String link;
        private String picture; // Adicionado o campo picture

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }
    }

    public static class Genre {
        private List<GenreData> data;

        public List<GenreData> getData() {
            return data;
        }

        public void setData(List<GenreData> data) {
            this.data = data;
        }
    }

    public static class GenreData {
        private Long id;
        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class TrackList {
        private List<Track> data;

        public List<Track> getData() {
            return data;
        }

        public void setData(List<Track> data) {
            this.data = data;
        }
    }

    public static class Track {
        private Long id;
        private String title;
        private String preview;
        private String link;

        public String getPreview() {
            return preview;
        }

        public void setPreview(String preview) {
            this.preview = preview;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
