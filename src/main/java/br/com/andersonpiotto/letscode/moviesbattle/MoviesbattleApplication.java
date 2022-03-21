package br.com.andersonpiotto.letscode.moviesbattle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/** Classe reponsavel por inicializar a aplicação
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 19/03/2022
 */
@SpringBootApplication
public class MoviesbattleApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoviesbattleApplication.class, args);
	}
	
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

}
