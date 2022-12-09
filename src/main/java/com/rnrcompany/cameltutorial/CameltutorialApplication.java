package com.rnrcompany.cameltutorial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
@EntityScan(basePackages = "com.rnrcompany.cameltutorial.beans")
public class CameltutorialApplication {

	public static void main(String[] args) {
		SpringApplication.run(CameltutorialApplication.class, args);
	}

}
