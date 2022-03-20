package br.com.andersonpiotto.letscode.moviesbattle.vo;

import lombok.Data;

@Data
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
