package ru.feshenko.credit.bank.calculator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI creditCalculationOpenApi() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Credit calculator API")
                                .description("REST API for credit scoring, loan offer generation, and credit calculation with payment schedule.")
                                .version("1.0.0")
                                .contact(
                                        new Contact()
                                                .name("Feshenko Artem Arenovich")
                                                .email("artyfeshenko@yandex.ru")
                                )
                );
    }
}
