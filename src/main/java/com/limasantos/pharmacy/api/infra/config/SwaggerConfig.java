package com.limasantos.pharmacy.api.infra.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI pharmacyOpenAPI(
            @Value("${api.docs.title}") String title,
            @Value("${api.docs.description}") String description,
            @Value("${api.docs.version}") String version,
            @Value("${api.docs.contact.name}") String contactName,
            @Value("${api.docs.contact.email}") String contactEmail,
            @Value("${api.docs.contact.url}") String contactUrl,
            @Value("${api.docs.developer.role}") String developerRole,
            @Value("${api.docs.project.url}") String projectUrl
    ) {
        Info info = new Info()
                .title(title)
                .description(description)
                .version(version)
                .contact(new Contact()
                        .name(contactName)
                        .email(contactEmail)
                        .url(contactUrl))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0"))
                .termsOfService(projectUrl)
                .extensions(Map.of(
                        "x-developer-role", developerRole,
                        "x-project-url", projectUrl
                ));

        return new OpenAPI()
                .info(info)
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Ambiente local"));

    }
}