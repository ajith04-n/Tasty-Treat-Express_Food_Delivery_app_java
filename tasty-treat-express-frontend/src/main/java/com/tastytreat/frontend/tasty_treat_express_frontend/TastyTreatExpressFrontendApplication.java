package com.tastytreat.frontend.tasty_treat_express_frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class TastyTreatExpressFrontendApplication {
	public static void main(String[] args) {
		SpringApplication.run(TastyTreatExpressFrontendApplication.class, args);
		System.out.println("*********Frontend Started**********");
	}

}
