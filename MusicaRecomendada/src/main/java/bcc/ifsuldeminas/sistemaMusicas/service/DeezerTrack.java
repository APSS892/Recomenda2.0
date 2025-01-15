package bcc.ifsuldeminas.sistemaMusicas.service;

public class DeezerTrack {
    private String id;
    private String title;
    private String preview;  // Novo campo// Novo campo
    private String link;  // Novo campo

    // Getters e setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPreview() {  // Novo getter
        return preview;
    }

    public void setPreview(String preview) {  // Novo setter
        this.preview = preview;
    }


    public String getLink() {  // Novo getter
        return link;
    }

    public void setLink(String link) {  // Novo setter
        this.link = link;
    }
}
