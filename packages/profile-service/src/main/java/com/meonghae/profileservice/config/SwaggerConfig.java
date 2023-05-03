// package com.meonghae.profileservice.config;
//
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.ComponentScan;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Import;
// import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
// import springfox.documentation.builders.ApiInfoBuilder;
// import springfox.documentation.builders.PathSelectors;
// import springfox.documentation.builders.RequestHandlerSelectors;
// import springfox.documentation.service.ApiInfo;
// import springfox.documentation.spi.DocumentationType;
// import springfox.documentation.spring.web.plugins.Docket;
// import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
// import java.util.function.Predicate;
//
// @Configuration
// @EnableSwagger2
// @ComponentScan(basePackages = {"com.meonghae"})
// @Import(BeanValidatorPluginsConfiguration.class)
// public class SwaggerConfig {
//    @Bean(name = "defaultApi")
//    public Docket defaultApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .ignoredParameterTypes(java.sql.Date.class)
//                .forCodeGeneration(true)
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.meonghae"))
//                .paths(PathSelectors.any())
//                .build()
//                .apiInfo(commnuityApiInfo())
//                .enable(true);
//    }
//
//    @Bean(name = "ProfileApi")
//    public Docket communityApi() {
//        Predicate<String> path = PathSelectors.ant("/profile-service/**");
//
//        return new Docket(DocumentationType.SWAGGER_2)
//                .groupName("Profile Service API")
//                .select()
//                .paths(path)
//                .build()
//                .apiInfo(commnuityApiInfo());
//    }
//
//    private ApiInfo commnuityApiInfo() {
//        return new ApiInfoBuilder()
//                .title("사용자 프로필 서비스 API")
//                .description("")
//                .version("1.0")
//                .build();
//    }
// }
