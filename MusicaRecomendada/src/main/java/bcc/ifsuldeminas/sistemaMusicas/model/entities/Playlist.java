package bcc.ifsuldeminas.sistemaMusicas.model.entities;

import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;
import java.util.List;

@Node
public class Playlist {


    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String nome;

    @Property
    private String descricao;

    @Relationship(type = "Possui", direction = Relationship.Direction.OUTGOING)
    private List<Musica> musica = new ArrayList<>();

    public Playlist() {}

    public Playlist(String nome, String descricao) {
        //this.id = id;
        this.nome = nome;
        this.descricao=descricao;
    }
    public void adicionarMusica(Musica musica) {
        this.musica.add(musica);
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<Musica> getMusicas() {
        return musica;
    }

    public void setMusicas(List<Musica> musicas) {
        this.musica = musicas;
    }
}
