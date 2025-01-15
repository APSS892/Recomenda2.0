package bcc.ifsuldeminas.sistemaMusicas.auth;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Neo4jConfig {

    @Bean
    public Driver neo4jDriver() {
        String uri = "bolt://localhost:7687";  // Altere para o URI do seu servidor Neo4j
        String username = "neo4j";            // Altere para seu usuário
        String password = "root1234";         // Altere para sua senha

        // Cria o driver Neo4j com as credenciais e URI fornecidos
        return GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }
}
