package com.moenghae.apigatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .pathMapping("/")
                .genericModelSubstitutes(ResponseEntity.class)
//                .securitySchemes(Lists.newArrayList(apiKey()))
//                .securityContexts(Lists.newArrayList(securityContext()))
                .useDefaultResponseMessages(false);
    }

//    private ApiKey apiKey() {
//        return new ApiKey("mykey", "api_key", "header");
//    }
//
//    private SecurityContext securityContext() {
//        return SecurityContext.builder()
//                .securityReferences(defaultAuth())
//                .forPaths(PathSelectors.regex("/anyPath.*"))
//                .build();
//    }
//
//    List<SecurityReference> defaultAuth() {
//        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        return Lists.newArrayList(
//                new SecurityReference("mykey", authorizationScopes));
//    }

    @Bean
    public UiConfiguration uiConfiguration() {
        return UiConfigurationBuilder.builder()
                .displayRequestDuration(true)
                .build();
    }

    @Bean
    @Primary
    public SwaggerResourcesProvider swaggerResourcesProvider() {
        List<SwaggerResource> resources = new ArrayList<>();
        // 각 서비스의 Swagger 문서를 수집합니다.
        SwaggerResource resource = new SwaggerResource();
        resource.setName("User Service");
        resource.setLocation("/user-service/v2/api-docs");
        resource.setSwaggerVersion("2.0");
        resources.add(resource);

        resource = new SwaggerResource();
        resource.setName("Community Service");
        resource.setLocation("/community-service/v2/api-docs");
        resource.setSwaggerVersion("2.0");
        resources.add(resource);

        resource = new SwaggerResource();
        resource.setName("Profile Service");
        resource.setLocation("/profile-service/v2/api-docs");
        resource.setSwaggerVersion("2.0");
        resources.add(resource);

        return () -> resources;
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/user-service/**")
                        .filters(f -> f.rewritePath("/user-service/(?<segment>.*)", "/${segment}")
                                .setPath("/v2/api-docs"))
                        .uri("lb://USER-SERVICE"))
                .route("community-service", r -> r.path("/community-service/**")
                        .filters(f -> f.rewritePath("/community-service/(?<segment>.*)", "/${segment}")
                                .setPath("/v2/api-docs"))
                        .uri("lb://COMMUNITY-SERVICE"))
                .route("profile-service", r -> r.path("/profile-service/**")
                        .filters(f -> f.rewritePath("/profile-service/(?<segment>.*)", "/${segment}")
                                .setPath("/v2/api-docs"))
                        .uri("lb://PROFILE-SERVICE"))
                .build();
    }
}
