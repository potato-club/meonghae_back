package com.meonghae.communityservice.core;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.*;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

@Configuration
@EnableSwagger2
@ComponentScan(basePackages = {"com.meonghae"})
@Import(BeanValidatorPluginsConfiguration.class)
public class Swagger2Config {
    @Bean
    public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping
            (WebEndpointsSupplier webEndpointsSupplier,
             ServletEndpointsSupplier servletEndpointsSupplier,
             ControllerEndpointsSupplier controllerEndpointsSupplier,
             EndpointMediaTypes endpointMediaTypes,
             CorsEndpointProperties corsProperties,
             WebEndpointProperties webEndpointProperties,
             Environment environment)
    {
        List<ExposableEndpoint<?>> allEndpoints = new ArrayList();
        Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
        allEndpoints.addAll(webEndpoints);
        allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
        allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
        String basePath = webEndpointProperties.getBasePath();
        EndpointMapping endpointMapping = new EndpointMapping(basePath);
        boolean shouldRegisterLinksMapping = this.shouldRegisterLinksMapping(
                webEndpointProperties, environment, basePath);
        return new WebMvcEndpointHandlerMapping(
                endpointMapping, webEndpoints, endpointMediaTypes,
                corsProperties.toCorsConfiguration(),
                new EndpointLinksResolver(allEndpoints, basePath),
                shouldRegisterLinksMapping, null);
    }

    private boolean shouldRegisterLinksMapping(WebEndpointProperties webEndpointProperties,
                                               Environment environment, String basePath)
    {
        return webEndpointProperties.getDiscovery().isEnabled()
                && (StringUtils.hasText(basePath) ||
                ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
    }
    @Bean(name = "defaultApi")
    public Docket defaultApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(java.sql.Date.class)
                .forCodeGeneration(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.meonghae"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(commnuityApiInfo())
                .enable(true);
    }

    @Bean(name = "communityApi")
    public Docket communityApi() {
        Predicate<String> path = PathSelectors.ant("/community-service/**");

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Community Service API")
                .select()
                .paths(path)
                .build()
                .apiInfo(commnuityApiInfo());
    }

    private ApiInfo commnuityApiInfo() {
        return new ApiInfoBuilder()
                .title("멍해 커뮤니티 서비스 API")
                .description("API 상세소개 및 사용법")
                .version("1.0")
                .build();
    }
}
