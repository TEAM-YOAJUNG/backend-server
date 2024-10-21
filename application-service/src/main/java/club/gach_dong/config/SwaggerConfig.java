package club.gach_dong.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
//public class SwaggerConfig {
//
//    @Value("${app.gateway.endpoint}") String gatewayEndpoint;
//    @Bean
//    public OpenAPI openAPI() {
//        return new OpenAPI()
//                .components(new Components())
//                .info(apiInfo())
//                .addServersItem(serverItem());
//    }
//
//    private Info apiInfo() {
//        return new Info()
//                .title("가츠동 API 명세 - 지원 서비스")
//                .description("지원 서비스에 대한 API 명세입니다.")
//                .version("v1");
//    }
//
//    private Server serverItem() {
//        return new Server()
//                .url(gatewayEndpoint + "/application/")
//                .description("지원 서비스 URL");
//    }
//}

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("PARKING LOTTO API")
                .description("PARKING LOTTO API 명세서")
                .version("1.0.0");
        String jwtSchemeName = "JWT TOKEN";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));
        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}