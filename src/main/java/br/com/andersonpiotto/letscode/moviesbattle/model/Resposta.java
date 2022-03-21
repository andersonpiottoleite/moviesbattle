package br.com.andersonpiotto.letscode.moviesbattle.model;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/** Classe que representa uma entidade resposta
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 19/03/2022
 */
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

	@Override
	public int hashCode() {
		return Objects.hash(correta, id, imdbIDRespondido);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Resposta other = (Resposta) obj;
		return Objects.equals(correta, other.correta) && Objects.equals(id, other.id)
				&& Objects.equals(imdbIDRespondido, other.imdbIDRespondido);
	}

}
