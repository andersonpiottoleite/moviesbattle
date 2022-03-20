package br.com.andersonpiotto.letscode.moviesbattle.vo;

public class RankeadoVO {

	private String nome;
	private int posicaoRanking;
	private int quantidadeDeQuizesRespondidos;
	private double porcentagemAcerto;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getPosicaoRanking() {
		return posicaoRanking;
	}

	public void setPosicaoRanking(int posicaoRanking) {
		this.posicaoRanking = posicaoRanking;
	}

	public int getQuantidadeDeQuizesRespondidos() {
		return quantidadeDeQuizesRespondidos;
	}

	public void setQuantidadeDeQuizesRespondidos(int quantidadeDeQuizesRespondidos) {
		this.quantidadeDeQuizesRespondidos = quantidadeDeQuizesRespondidos;
	}

	public double getPorcentagemAcerto() {
		return porcentagemAcerto;
	}

	public void setPorcentagemAcerto(double porcentagemAcerto) {
		this.porcentagemAcerto = porcentagemAcerto;
	}

}
