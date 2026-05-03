package ru.feshenko.credit.bank.statement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI statementApiOpenApi() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Statement API")
                                .description("REST API for pre-scoring and routing to deal service: " +
                                        "creating loan statements and selecting loan offers.")
                                .version("1.0.0")
                                .contact(
                                        new Contact()
                                                .name("Feshenko Artem Arenovich")
                                                .email("artyfeshenko@yandex.ru")
                                )
                );
    }
}
