package bcc.ifsuldeminas.sistemaMusicas.model.entities;

import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Node
public class Usuario {

    @Id
    @GeneratedValue
    private long id;


    @Property
    private String nome;  // Nome do usuário

    @Property
    private LocalDate dataNascimento;  // Data de nascimento do usuário

    @Property
    private String genero;  // Gênero do usuário

    @Property
    private String senha;


    @Relationship(type = "CRIAR"  , direction = Relationship.Direction.OUTGOING)
    private List<Playlist> playlists = new ArrayList<>();


    @Relationship(type = "ADICIONOU"  , direction = Relationship.Direction.OUTGOING)
    private List<Musica> musica = new ArrayList<>();

    public List<Musica> getMusica() {
        return musica;
    }

    public void setMusica(List<Musica> musica) {
        this.musica = musica;
    }
    public void adicionarMusica(Musica musica) {
        this.musica.add(musica);
    }
    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    public void adicionarPlaylist(Playlist playlist) {
        this.playlists.add(playlist);
    }

    // Construtor vazio necessário para o Neo4j
    public Usuario() {}


    public Usuario(String nome, LocalDate dataNascimento, String genero, String senha) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.genero = genero;
        this.senha = senha;
    }




    // Getters e setters
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

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getIdade() {
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
    public void removerMusica(Musica musica) {
        this.musica.remove(musica);
    }

}
