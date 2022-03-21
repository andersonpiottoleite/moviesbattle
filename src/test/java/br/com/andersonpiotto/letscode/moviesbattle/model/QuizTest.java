package br.com.andersonpiotto.letscode.moviesbattle.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Classe de testes para <code>Quiz</code>
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 21/03/2022
 */

class QuizTest {

	private Quiz quiz = null;

	@BeforeEach
	void antesDeCadaUm() {
		quiz = new Quiz();
	}

	@Test
	void quizDeveSerCriadoComoNaoEncerrado() {
		assertEquals(quiz.isEncerrado(), false);
	}

	@Test
	void quizDeveSerSetadoComoEncerrado() {
		quiz.encerra();
		assertEquals(quiz.isEncerrado(), true);
	}

}
