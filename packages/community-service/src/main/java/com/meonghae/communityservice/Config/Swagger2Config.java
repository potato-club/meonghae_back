package com.meonghae.communityservice.Config;

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
