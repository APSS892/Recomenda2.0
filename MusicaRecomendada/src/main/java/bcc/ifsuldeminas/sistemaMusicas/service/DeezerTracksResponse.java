package bcc.ifsuldeminas.sistemaMusicas.service;

import java.util.List;

public class DeezerTracksResponse {
    public List<DeezerTrack> getData() {
        return data;
    }

    public void setData(List<DeezerTrack> data) {
        this.data = data;
    }

    private List<DeezerTrack> data;

    // Getters e setters
}
