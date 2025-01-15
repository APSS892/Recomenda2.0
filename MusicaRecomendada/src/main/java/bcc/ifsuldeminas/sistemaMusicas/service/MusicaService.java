package bcc.ifsuldeminas.sistemaMusicas.service;

import bcc.ifsuldeminas.sistemaMusicas.model.entities.Artista;
import bcc.ifsuldeminas.sistemaMusicas.model.entities.Genero;
import bcc.ifsuldeminas.sistemaMusicas.model.entities.Musica;
import bcc.ifsuldeminas.sistemaMusicas.repository.ArtistaRepository;
import bcc.ifsuldeminas.sistemaMusicas.repository.GeneroRepository;
import bcc.ifsuldeminas.sistemaMusicas.repository.MusicaRepository;
//import net.minidev.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.AbstractByteBuf;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
/*
@Service
public class MusicaService {

    private final MusicaRepository musicaRepository;

    @Autowired
    public MusicaService(MusicaRepository musicaRepository) {
        this.musicaRepository = musicaRepository;
    }

    public Musica salvarMusica(Musica musica) {
        return musicaRepository.save(musica);
    }

    public Optional<Musica> buscarMusicaPorId(String id) {
        return musicaRepository.findById(id);
    }

    public Iterable<Musica> listarMusicas() {
        return musicaRepository.findAll();
    }

    public void deletarMusica(String id) {
        musicaRepository.deleteById(id);
    }
}
*/

import bcc.ifsuldeminas.sistemaMusicas.model.entities.Musica;
import bcc.ifsuldeminas.sistemaMusicas.repository.MusicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class MusicaService {

    @Autowired
    private ArtistaRepository artistaRepository;

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private MusicaRepository musicaRepository;

    private final String BASE_URL = "https://api.deezer.com";

    public Object salvarArtistaEGeneros(String artistId) {
        RestTemplate restTemplate = new RestTemplate();

        String artistaUrl = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/artist/" + artistId).toUriString();

        var response = restTemplate.getForObject(artistaUrl, DeezerArtistResponse.class);

        if (response == null) {
            throw new RuntimeException("Artista não encontrado.");
        }

        Artista artista = new Artista(response.getName(), response.getId(), response.getLink(), response.getPicture());

        List<Genero> generos = new ArrayList<>();
        if (response.getGenres() != null && response.getGenres().getData() != null) {
            for (DeezerGenre genre : response.getGenres().getData()) {
                Genero genero = generoRepository.findBySpotifyId(genre.getId());
                if (genero == null) {
                    genero = new Genero(genre.getName(), genre.getId());
                    generoRepository.save(genero);
                }
                generos.add(genero);  // Adiciona cada gênero à lista
            }
        }

        // Aqui, estamos assegurando que todos os gêneros sejam associados ao artista
        artista.setGeneros(generos);
        artistaRepository.save(artista);

        return artista;
    }


    public void salvarMusicasDoArtista(String artistId) {
        RestTemplate restTemplate = new RestTemplate();

        String musicasUrl = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/artist/" + artistId + "/top").toUriString();

        var response = restTemplate.getForObject(musicasUrl, DeezerTracksResponse.class);

        if (response == null || response.getData() == null) {
            throw new RuntimeException("Nenhuma música encontrada para o artista.");
        }

        Optional<Artista> artista = artistaRepository.findBySpotifyId(artistId);

        if (artista.isEmpty()) {
            throw new RuntimeException("Artista não encontrado no banco de dados.");
        }

        for (DeezerTrack track : response.getData()) {
            Musica musica = new Musica(track.getTitle(), track.getId(),track.getPreview(), track.getLink(), new ArrayList<>(), new ArrayList<>());
            musica.getArtistas().add(artista.get());
            musicaRepository.save(musica);
        }
    }

    public void salvarDadosAPartirDeAlbuns(int albumInicio, int albumFim) {
        RestTemplate restTemplate = new RestTemplate();

        for (int albumId = albumInicio; albumId <= albumFim; albumId++) {
            String url = "https://api.deezer.com/album/" + albumId;

            try {
                ResponseEntity<DeezerAlbumResponse> response = restTemplate.getForEntity(url, DeezerAlbumResponse.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    DeezerAlbumResponse albumData = response.getBody();

                    DeezerAlbumResponse.Artist artistData = albumData.getArtist();
                    Artista artista = salvarArtistaSeNaoExistir(artistData);

                    artista.adicionarAlbumId(String.valueOf(albumId));
                    artistaRepository.save(artista);

                    List<Genero> generos = new ArrayList<>();
                    for (DeezerAlbumResponse.GenreData genreData : albumData.getGenres().getData()) {
                        Genero genero = salvarGeneroSeNaoExistir(genreData);
                        generos.add(genero);
                        artista.adicionarGenero(genero);
                    }
                    artistaRepository.save(artista);

                    for (DeezerAlbumResponse.Track trackData : albumData.getTracks().getData()) {
                        salvarMusicaSeNaoExistir(trackData, artista, generos);
                        System.out.println("Sucesso " + albumId);
                    }
                }
            } catch (Exception e) {
               System.out.println("Erro ao processar o album de ID " + albumId + ": " + e.getMessage());
            }
        }
    }

    private Artista salvarArtistaSeNaoExistir(DeezerAlbumResponse.Artist artistData) {
        return artistaRepository.findBySpotifyId(artistData.getId().toString())
                .orElseGet(() -> {
                    Artista artista = new Artista();
                    artista.setNome(artistData.getName());
                    artista.setSpotifyId(artistData.getId().toString());
                    artista.setLink(artistData.getLink());
                    artista.setPicture(artistData.getPicture());
                    return artistaRepository.save(artista);
                });
    }

    private Genero salvarGeneroSeNaoExistir(DeezerAlbumResponse.GenreData genreData) {
        return generoRepository.findByNome(genreData.getName())
                .orElseGet(() -> {
                    Genero genero = new Genero();
                    genero.setNome(genreData.getName());
                    genero.setSpotifyId(genreData.getId().toString());
                    return generoRepository.save(genero);
                });
    }

    private void salvarMusicaSeNaoExistir(DeezerAlbumResponse.Track trackData, Artista artista, List<Genero> generos) {
        if (!musicaRepository.existsBySpotifyId(trackData.getId().toString())) {
            Musica musica = new Musica();
            musica.setTitulo(trackData.getTitle());
            musica.setSpotifyId(trackData.getId().toString());
            musica.setPreview(trackData.getPreview());
            musica.setLink(trackData.getLink());
            musica.getArtistas().add(artista);
            musica.getGeneros().addAll(generos);
            musicaRepository.save(musica);
        }
    }


}





