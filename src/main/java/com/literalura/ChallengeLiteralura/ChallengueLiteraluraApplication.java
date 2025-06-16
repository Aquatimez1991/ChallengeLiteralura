package com.literalura.ChallengeLiteralura;

import com.literalura.ChallengeLiteralura.principal.Principal;
import io.github.cdimascio.dotenv.Dotenv;
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

		Dotenv dotenv = Dotenv.load();
		System.setProperty("DB_NAME", dotenv.get("DB_NAME"));
		System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
		System.setProperty("DB_USER", dotenv.get("DB_USER"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));


		SpringApplication.run(ChallengueLiteraluraApplication.class, args);
	}

	@Override
	public void run(String... args) {
		principal.muestraElMenu();
	}
}