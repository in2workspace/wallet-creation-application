package es.in2.wca.configuration;

import es.in2.wca.configuration.properties.OpenApiProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    private final OpenApiProperties openApiProperties;

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .contact(new Contact()
                                .email(openApiProperties.info().contact().email())
                                .name(openApiProperties.info().contact().name())
                                .url(openApiProperties.info().contact().url()))
                        .license(new License()
                                .name(openApiProperties.info().license().name())
                                .url(openApiProperties.info().license().url())))
                .servers(List.of(new Server()
                        .url(openApiProperties.server().url())
                        .description(openApiProperties.server().description())));
    }

}
