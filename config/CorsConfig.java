package com.myfin.customer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/admin-api/**").allowedOrigins("http://localhost:8082").allowedMethods("*")
						.allowedHeaders("*").allowCredentials(true);

				registry.addMapping("/loans/**").allowedOrigins("http://localhost:8082").allowedMethods("*")
						.allowedHeaders("*").allowCredentials(true);
			}
		};
	}
}
