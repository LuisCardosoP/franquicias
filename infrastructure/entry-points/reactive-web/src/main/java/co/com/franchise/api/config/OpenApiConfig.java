package co.com.franchise.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI franchiseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Franchise Management API")
                        .version("1.0.0")
                        .description("Reactive RESTful API for managing a franchise network. "
                                + "Provides operations for franchises, branches, and products "
                                + "including stock management and highest-stock queries.")
                        .contact(new Contact()
                                .name("Franchise API Team")));
    }
}
