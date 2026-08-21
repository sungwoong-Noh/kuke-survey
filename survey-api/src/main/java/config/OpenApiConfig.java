package config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Kuke Survey Service API")
                        .description("설문조사 생성, 배포, 응답 수집을 위한 백엔드 REST API 명세서이다.")
                        .version("v1.0.0")
                        .contact(
                                new Contact().name("Kuke Survey Team")
                        )
                );
    }

}
