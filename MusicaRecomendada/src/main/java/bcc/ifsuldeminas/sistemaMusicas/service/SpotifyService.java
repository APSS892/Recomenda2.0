package bcc.ifsuldeminas.sistemaMusicas.service;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

//import java.net.http.HttpHeaders;


@Service
public class SpotifyService {

    @Value("${spotify.api.base.url}")
    private String baseUrl;

    @Value("${spotify.api.token}")
    private String token;

    private final RestTemplate restTemplate;

    public SpotifyService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public JSONObject buscarMusicaPorId(String spotifyId) throws JSONException {
        String url = baseUrl + "/tracks/" + spotifyId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);


        HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return new JSONObject(response.getBody());
    }
}
