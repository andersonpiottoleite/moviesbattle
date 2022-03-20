package br.com.andersonpiotto.letscode.moviesbattle.vo;

public class QuizResponseVO {

	private ResumoFilmeVO primeiroFilme;

	private ResumoFilmeVO segundoFilme;

	private Long idQuiz;
	
	private Long idPergunta;

	public QuizResponseVO() {
	}

	public QuizResponseVO(ResumoFilmeVO primeiroFilme, ResumoFilmeVO segundoFilme, Long idQuiz, Long idPergunta) {
		this.primeiroFilme = primeiroFilme;
		this.segundoFilme = segundoFilme;
		this.idQuiz = idQuiz;
		this.idPergunta = idPergunta;
	}

	public ResumoFilmeVO getPrimeiroFilme() {
		return primeiroFilme;
	}

	public void setPrimeiroFilme(ResumoFilmeVO primeiroFilme) {
		this.primeiroFilme = primeiroFilme;
	}

	public ResumoFilmeVO getSegundoFilme() {
		return segundoFilme;
	}

	public void setSegundoFilme(ResumoFilmeVO segundoFilme) {
		this.segundoFilme = segundoFilme;
	}

	public Long getIdQuiz() {
		return idQuiz;
	}

	public void setIdQuiz(Long idQuiz) {
		this.idQuiz = idQuiz;
	}

	public Long getIdPergunta() {
		return idPergunta;
	}

	public void setIdPergunta(Long idPergunta) {
		this.idPergunta = idPergunta;
	}

}
