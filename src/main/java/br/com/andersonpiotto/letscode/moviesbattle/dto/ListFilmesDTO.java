package br.com.andersonpiotto.letscode.moviesbattle.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ListFilmesDTO {

	@JsonProperty("Search")
	private List<FilmeDTO> filmes = new ArrayList<>();

	private int totalResults;

	@JsonProperty("Response")
	private Boolean response;

	public List<FilmeDTO> getFilmes() {
		return filmes;
	}

	public void setFilmes(List<FilmeDTO> filmes) {
		this.filmes = filmes;
	}

	public int getTotalResults() {
		return totalResults;
	}

	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}

	public Boolean getResponse() {
		return response;
	}

	public void setResponse(Boolean response) {
		this.response = response;
	}

}
