package br.com.andersonpiotto.letscode.moviesbattle.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/** Classe que representa uma entidade quiz
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 19/03/2022
 */
@Entity
public class Quiz {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = CascadeType.PERSIST)
	private Usuario usuario;

	@OneToMany(cascade = {CascadeType.ALL}, mappedBy = "quiz", fetch = FetchType.EAGER)
	private List<Pergunta> perguntas = new ArrayList<>();
	
	private boolean encerrado;

	private int quantidadeRespostasCorretas;

	private int quantidadeErros;

	public Quiz(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public Quiz() {
	}

	public Long getId() {
		return id;
	}
	public List<Pergunta> getPerguntas() {
		return perguntas;
	}

	public void addPergunta(Pergunta pergunta) {
		this.perguntas.add(pergunta);
	}

	public int getQuantidadeRespostasCorretas() {
		return quantidadeRespostasCorretas;
	}

	public void setQuantidadeRespostasCorretas(int quantidadeRespostasCorretas) {
		this.quantidadeRespostasCorretas = quantidadeRespostasCorretas;
	}

	public int getQuantidadeErros() {
		return quantidadeErros;
	}

	public void setQuantidadeErros(int quantidadeErros) {
		this.quantidadeErros = quantidadeErros;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public boolean isEncerrado() {
		return encerrado;
	}

	public void encerra() {
		this.encerrado = true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(encerrado, id, perguntas, quantidadeErros, quantidadeRespostasCorretas, usuario);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Quiz other = (Quiz) obj;
		return encerrado == other.encerrado && Objects.equals(id, other.id)
				&& Objects.equals(perguntas, other.perguntas) && quantidadeErros == other.quantidadeErros
				&& quantidadeRespostasCorretas == other.quantidadeRespostasCorretas
				&& Objects.equals(usuario, other.usuario);
	}

}
