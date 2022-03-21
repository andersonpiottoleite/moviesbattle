package br.com.andersonpiotto.letscode.moviesbattle.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Classe de testes para <code>Pergunta</code>
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 21/03/2022
 */

class PerguntaTest {
	
	private Pergunta pergunta = null;
	
	@BeforeEach
	void antesDeCadaUm() {
		pergunta = new Pergunta();
	}

	@Test
	void perguntaDeveSerCriadaComoNaoRespondida() {
		assertEquals(pergunta.isRespondida(), false);
	}

	@Test
	void perguntaDeveSerSetadaComoRespondida() {
		pergunta.respondida();
		assertEquals(pergunta.isRespondida(), true);
	}

}
