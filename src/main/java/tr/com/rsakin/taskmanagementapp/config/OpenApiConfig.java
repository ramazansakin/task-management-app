package tr.com.rsakin.taskmanagementapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Management API")
                        .description("API for managing tasks")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Ramazan Sakin")
                                .email("ramazansakin63@gmail.com")
                                .url("https://github.com/ramazansakin")));
    }

}
