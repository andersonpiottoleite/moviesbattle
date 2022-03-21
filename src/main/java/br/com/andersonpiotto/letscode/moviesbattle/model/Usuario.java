package br.com.andersonpiotto.letscode.moviesbattle.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/** Classe que representa uma entidade usuario
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 19/03/2022
 */
@Entity
public class Usuario {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String login;
	private String senha;
	private String nome;

	public Usuario(String login, String senha, String nome) {
		this.login = login;
		this.senha = senha;
		this.nome = nome;
	}
	
	public Usuario() {
	}

	public Long getId() {
		return id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
