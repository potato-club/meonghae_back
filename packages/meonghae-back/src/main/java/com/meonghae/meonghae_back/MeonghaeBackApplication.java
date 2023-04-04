package com.meonghae.meonghae_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class MeonghaeBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeonghaeBackApplication.class, args);
	}

}
