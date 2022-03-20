package br.com.andersonpiotto.letscode.moviesbattle.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FilmeAvaliadoDTO {

	@JsonProperty("Title")
	private String titulo;

	@JsonProperty("Year")
	private String ano;

	private String imdbID;

	@JsonProperty("Type")
	private String tipo;

	@JsonProperty("imdbRating")
	private Double imdbRating;
	
	@JsonProperty("Poster")
	private String poster;

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

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Double getImdbRating() {
		return imdbRating;
	}

	public void setImdbRating(Double imdbRating) {
		this.imdbRating = imdbRating;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

}
