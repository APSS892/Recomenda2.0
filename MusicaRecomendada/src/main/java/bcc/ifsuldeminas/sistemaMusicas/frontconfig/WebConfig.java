package bcc.ifsuldeminas.sistemaMusicas.frontconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Permite requisições de qualquer origem (ou de origens específicas)
        registry.addMapping("/**")  // Permite CORS para todas as rotas
                .allowedOrigins("http://localhost:3060")  // Substitua pela origem do seu frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}


/*
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc // Ativa o suporte a MVC no Spring
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Aplica CORS a todos os endpoints
                .allowedOrigins("http://localhost:3060") // Permite a origem do frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                .allowedHeaders("*") // Permite todos os cabeçalhos
                .allowCredentials(true); // Permite envio de cookies (se necessário)
    }
}
*/