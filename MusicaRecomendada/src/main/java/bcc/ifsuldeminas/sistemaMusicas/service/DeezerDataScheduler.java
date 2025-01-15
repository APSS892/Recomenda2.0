package bcc.ifsuldeminas.sistemaMusicas.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeezerDataScheduler {

    @Autowired
    private MusicaService deezerService;

    @Scheduled(fixedDelay = 60000)
    public void executarBuscaDeAlbuns() {
        deezerService.salvarDadosAPartirDeAlbuns(1, 20000);
    }
    //Precisa trocar os valores de parametro, eu usei de 1 a 100000 mas vai demorar bastante.
}