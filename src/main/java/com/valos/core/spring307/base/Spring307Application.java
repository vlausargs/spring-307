package com.valos.core.spring307.base;

import com.valos.core.spring307.base.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class Spring307Application {

	public static void main(String[] args) {
		SpringApplication.run(Spring307Application.class, args);
	}

}
