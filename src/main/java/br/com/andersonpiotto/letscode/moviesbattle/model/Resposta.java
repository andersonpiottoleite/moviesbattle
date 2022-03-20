package br.com.andersonpiotto.letscode.moviesbattle.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Resposta {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String imdbIDRespondido;

	private Boolean correta;

	public Long getId() {
		return id;
	}

	public String getImdbIDRespondido() {
		return imdbIDRespondido;
	}

	public void setImdbIDRespondido(String imdbIDRespondido) {
		this.imdbIDRespondido = imdbIDRespondido;
	}

	public Boolean getCorreta() {
		return correta;
	}

	public void setCorreta(Boolean correta) {
		this.correta = correta;
	}

}
