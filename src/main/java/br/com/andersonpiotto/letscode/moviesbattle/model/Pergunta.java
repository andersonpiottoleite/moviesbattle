package br.com.andersonpiotto.letscode.moviesbattle.model;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;


/** Classe que representa uma entidade pergunta
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 19/03/2022
 */
@Entity
public class Pergunta {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String primeiraOpcaoImdbID;

	private String segundaOpcaoImdbID;

	@OneToOne(cascade = CascadeType.PERSIST)
	private Resposta resposta;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Quiz quiz;
	
	private boolean respondida;

	public Pergunta() {
	}

	public Pergunta(String primeiraOpcaoImdbID, String segundaOpcaoImdbID) {
		this.primeiraOpcaoImdbID = primeiraOpcaoImdbID;
		this.segundaOpcaoImdbID = segundaOpcaoImdbID;
	}

	public Long getId() {
		return id;
	}

	public String getPrimeiraOpcaoImdbID() {
		return primeiraOpcaoImdbID;
	}

	public void setPrimeiraOpcaoImdbID(String primeiraOpcaoImdbID) {
		this.primeiraOpcaoImdbID = primeiraOpcaoImdbID;
	}

	public String getSegundaOpcaoImdbID() {
		return segundaOpcaoImdbID;
	}

	public void setSegundaOpcaoImdbID(String segundaOpcaoImdbID) {
		this.segundaOpcaoImdbID = segundaOpcaoImdbID;
	}

	public Resposta getResposta() {
		return resposta;
	}

	public void setResposta(Resposta resposta) {
		this.resposta = resposta;
	}

	public Quiz getQuiz() {
		return quiz;
	}

	public void setQuiz(Quiz quiz) {
		this.quiz = quiz;
	}

	public boolean isRespondida() {
		return respondida;
	}

	public void respondida() {
		this.respondida = true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, primeiraOpcaoImdbID, quiz, respondida, resposta, segundaOpcaoImdbID);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pergunta other = (Pergunta) obj;
		return Objects.equals(id, other.id) && Objects.equals(primeiraOpcaoImdbID, other.primeiraOpcaoImdbID)
				&& Objects.equals(quiz, other.quiz) && respondida == other.respondida
				&& Objects.equals(resposta, other.resposta)
				&& Objects.equals(segundaOpcaoImdbID, other.segundaOpcaoImdbID);
	}

}
