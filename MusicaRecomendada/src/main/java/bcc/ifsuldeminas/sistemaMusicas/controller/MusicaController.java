package bcc.ifsuldeminas.sistemaMusicas.controller;

import bcc.ifsuldeminas.sistemaMusicas.model.entities.Artista;
import bcc.ifsuldeminas.sistemaMusicas.model.entities.Genero;
import bcc.ifsuldeminas.sistemaMusicas.model.entities.Musica;
import bcc.ifsuldeminas.sistemaMusicas.repository.ArtistaRepository;
import bcc.ifsuldeminas.sistemaMusicas.repository.GeneroRepository;
import bcc.ifsuldeminas.sistemaMusicas.repository.MusicaRepository;
import bcc.ifsuldeminas.sistemaMusicas.service.MusicaService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
/*
@RestController
@RequestMapping("/musicas")
public class MusicaController {

    private final MusicaService musicaService;

    @Autowired
    public MusicaController(MusicaService musicaService) {
        this.musicaService = musicaService;
    }

    @GetMapping
    public ResponseEntity<Iterable<Musica>> listarMusicas() {
        Iterable<Musica> musicas = musicaService.listarMusicas();
        return new ResponseEntity<>(musicas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Musica> buscarMusicaPorId(@PathVariable String id) {
        Optional<Musica> musica = musicaService.buscarMusicaPorId(id);
        return musica.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Musica> salvarMusica(@RequestBody Musica musica) {
        Musica musicaSalva = musicaService.salvarMusica(musica);
        return new ResponseEntity<>(musicaSalva, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMusica(@PathVariable String id) {
        Optional<Musica> musica = musicaService.buscarMusicaPorId(id);
        if (musica.isPresent()) {
            musicaService.deletarMusica(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

    @RestController
    @RequestMapping("/deezer")
    public class MusicaController {

        @Autowired
        private MusicaService deezerService;
        @Autowired
        private ArtistaRepository artistaRepository;

        @Autowired
        private GeneroRepository generoRepository;

        @Autowired
        private MusicaRepository musicaRepository;
        @PostMapping("/artista-completo/{artistId}")
        public String salvarArtistaComMusicas(@PathVariable String artistId) {
            try {
                deezerService.salvarArtistaEGeneros(artistId);
                deezerService.salvarMusicasDoArtista(artistId);
                return "Artista e músicas salvos com sucesso!";
            } catch (Exception e) {
                return "Erro: " + e.getMessage();
            }
        }
        @GetMapping("/album/{albumId}")
        public ResponseEntity<String> salvarAlbum(@PathVariable String albumId) {

            try {
                // Fazer requisição para a API da Deezer
                RestTemplate restTemplate = new RestTemplate();
                String url = "https://api.deezer.com/album/" + albumId;
                ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    Map<String, Object> albumData = response.getBody();

                    // Salvar o artista
                    Map<String, Object> artistData = (Map<String, Object>) albumData.get("artist");
                    Artista artista = new Artista(
                            (String) artistData.get("name"),
                            String.valueOf(artistData.get("id")),
                            (String) artistData.get("link"),
                            (String) artistData.get("picture")
                    );
                    artista = artistaRepository.save(artista);

                    // Salvar os gêneros
                    Map<String, Object> genresData = (Map<String, Object>) albumData.get("genres");
                    List<Map<String, Object>> genres = (List<Map<String, Object>>) genresData.get("data");
                    List<Genero> generos = new ArrayList<>();
                    for (Map<String, Object> genreData : genres) {
                        Genero genero = new Genero(
                                (String) genreData.get("name"),
                                String.valueOf(genreData.get("id"))
                        );
                        genero = generoRepository.save(genero);
                        generos.add(genero);
                    }
                    // Salvar as músicas
                    Map<String, Object> tracksData = (Map<String, Object>) albumData.get("tracks");
                    List<Map<String, Object>> tracks = (List<Map<String, Object>>) tracksData.get("data");
                    for (Map<String, Object> trackData : tracks) {
                        Musica musica = new Musica(
                                (String) trackData.get("title"),
                                String.valueOf(trackData.get("id")),
                                String.valueOf(trackData.get("link")),
                                String.valueOf(trackData.get("preview")),
                                generos,
                                List.of(artista) // Associar música ao artista
                        );
                        musicaRepository.save(musica);
                    }

                    return ResponseEntity.ok("Álbum salvo com sucesso!");
                }

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Álbum não encontrado na Deezer");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar o álbum");
            }

        }

    }