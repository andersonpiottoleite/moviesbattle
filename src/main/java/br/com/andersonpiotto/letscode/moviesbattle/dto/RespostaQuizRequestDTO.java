package br.com.andersonpiotto.letscode.moviesbattle.dto;

public class RespostaQuizRequestDTO {

	private Long idPergunta;

	private Long idQuiz;

	private String imdbIDRespondido;

	public RespostaQuizRequestDTO() {
	}

	public String getImdbIDRespondido() {
		return imdbIDRespondido;
	}

	public void setImdbIDRespondido(String imdbIDRespondido) {
		this.imdbIDRespondido = imdbIDRespondido;
	}

	public Long getIdPergunta() {
		return idPergunta;
	}

	public void setIdPergunta(Long idPergunta) {
		this.idPergunta = idPergunta;
	}

	public Long getIdQuiz() {
		return idQuiz;
	}

	public void setIdQuiz(Long idQuiz) {
		this.idQuiz = idQuiz;
	}

}
