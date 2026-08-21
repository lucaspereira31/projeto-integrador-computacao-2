package br.com.projeto.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI babyPremiumOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Baby Premium API")
                        .description("API REST para acompanhamento de rotina e saude do bebe")
                        .version("v1.0"));
    }
}
