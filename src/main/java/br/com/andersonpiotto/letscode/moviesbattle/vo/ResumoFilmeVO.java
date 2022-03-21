package br.com.andersonpiotto.letscode.moviesbattle.vo;

import br.com.andersonpiotto.letscode.moviesbattle.dto.FilmeAvaliadoDTO;
import br.com.andersonpiotto.letscode.moviesbattle.dto.FilmeDTO;


/** Classe que representa um View Object para Filmes
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 19/03/2022
 */

public class ResumoFilmeVO {

	private String titulo;
	private String ano;
	private String imdbID;
	private String poster;

	public ResumoFilmeVO() {
	}

	public ResumoFilmeVO(String titulo, String ano, String imdbID, String poster) {
		this.titulo = titulo;
		this.ano = ano;
		this.imdbID = imdbID;
		this.poster = poster;
	}
	
	public static ResumoFilmeVO criaResumoFilmeVO(FilmeDTO primeiroFilme) {
		return new ResumoFilmeVO(primeiroFilme.getTitulo(), primeiroFilme.getAno(), primeiroFilme.getImdbID(), primeiroFilme.getPoster());
	}
	
	public static ResumoFilmeVO criaResumoFilmeVO(FilmeAvaliadoDTO filmeAvaliado) {
		return new ResumoFilmeVO(filmeAvaliado.getTitulo(), filmeAvaliado.getAno(), filmeAvaliado.getImdbID(), filmeAvaliado.getPoster());
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public String getImdbID() {
		return imdbID;
	}

	public void setImdbID(String imdbID) {
		this.imdbID = imdbID;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

}
