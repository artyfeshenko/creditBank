package ru.feshenko.credit.bank.deal.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI dealApiOpenApi() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Deal API")
                                .description("REST API for managing credit applications: creating statements," +
                                        " selecting loan offers, completing registration, and full credit calculation.")
                                .version("1.0.0")
                                .contact(
                                        new Contact()
                                                .name("Feshenko Artem Arenovich")
                                                .email("artyfeshenko@yandex.ru")
                                )
                );
    }
}
