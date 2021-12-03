package com.plango.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    private static final String SWAGGER_DOC_VERSION = "1";
    private static final String SWAGGER_DOC_DESCRIPTION = "API Plango pour l'application Plango, organisateur de voyage";
    private static final String SWAGGER_DOC_NAME = "Plango API";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.plango.api"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                SWAGGER_DOC_NAME,
                SWAGGER_DOC_DESCRIPTION,
                SWAGGER_DOC_VERSION,
                "Terms of service",
                new Contact("PlangoTeam", "https://www.linkedin.com/company/plangotravelapp", ""),
                "License of API", "API license URL", new ArrayList<>());
    }
}
