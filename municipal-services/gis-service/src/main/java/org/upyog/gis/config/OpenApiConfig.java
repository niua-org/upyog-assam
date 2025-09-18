package org.upyog.gis.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for OpenAPI (Swagger) documentation for the GIS Service API.
 * Sets up the OpenAPI bean with service metadata and server information.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI documentation for the GIS Service API.
     *
     * @return an OpenAPI instance with custom server and API info
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(new Server().url("/gis-service")))
                .info(new Info()
                        .title("GIS Service API")
                        .description("API details of the GIS service")
                        .version("1.0.0"));
    }
}