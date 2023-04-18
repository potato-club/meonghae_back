package com.meonghae.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.function.Predicate;

@Configuration
@EnableSwagger2
@ComponentScan(basePackages = {"com.meonghae"})
@Import(BeanValidatorPluginsConfiguration.class)
public class Swagger2Config {

    @Bean(name = "defaultApi")
    public Docket defaultApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(java.sql.Date.class)
                .forCodeGeneration(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.meonghae"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(userApiInfo())
                .enable(true);
    }

    @Bean(name = "userApi")
    public Docket userApi() {
        Predicate<String> path = PathSelectors.ant("/user-service/**");

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("User Service API")
                .select()
                .paths(path)
                .build()
                .apiInfo(userApiInfo());
    }

    private ApiInfo userApiInfo(){
        return new ApiInfoBuilder()
                .title("멍해 유저 서비스 API")
                .description("API 상세소개 및 사용법")
                .version("1.0")
                .build();
    }
}