package com.literalura.ChallengeLiteralura;

import com.literalura.ChallengeLiteralura.principal.Principal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChallengueLiteraluraApplication implements CommandLineRunner {

	private final Principal principal;

	public ChallengueLiteraluraApplication(Principal principal) {
		this.principal = principal;
	}

	public static void main(String[] args) {
		SpringApplication.run(ChallengueLiteraluraApplication.class, args);
	}

	@Override
	public void run(String... args) {
		principal.muestraElMenu();
	}
}
