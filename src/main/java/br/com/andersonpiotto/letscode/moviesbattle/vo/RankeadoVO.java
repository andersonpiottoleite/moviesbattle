package br.com.andersonpiotto.letscode.moviesbattle.vo;

/**
 * Classe que representa um View Object para ranking
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 19/03/2022
 */
public class RankeadoVO {

	private String nome;
	private int posicaoRanking;
	private int quantidadeDeQuizesRespondidos;
	private int quantidadeTotalRespostas;
	private int quantidadeTotalRepostasCertas;
	private int quantidadeTotalRepostasErradas;
	private int porcentagemAcerto;
	private double pontuacao;

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

	public double getPontuacao() {
		return pontuacao;
	}

	public void setPontuacao(double pontuacao) {
		this.pontuacao = pontuacao;
	}

	public int getQuantidadeTotalRespostas() {
		return quantidadeTotalRespostas;
	}

	public void setQuantidadeTotalRespostas(int quantidadeTotalRespostas) {
		this.quantidadeTotalRespostas = quantidadeTotalRespostas;
	}

	public int getQuantidadeTotalRepostasCertas() {
		return quantidadeTotalRepostasCertas;
	}

	public void setQuantidadeTotalRepostasCertas(int quantidadeTotalRepostasCertas) {
		this.quantidadeTotalRepostasCertas = quantidadeTotalRepostasCertas;
	}

	public int getQuantidadeTotalRepostasErradas() {
		return quantidadeTotalRepostasErradas;
	}

	public void setQuantidadeTotalRepostasErradas(int quantidadeTotalRepostasErradas) {
		this.quantidadeTotalRepostasErradas = quantidadeTotalRepostasErradas;
	}

	public int getPorcentagemAcerto() {
		return porcentagemAcerto;
	}

	public void setPorcentagemAcerto(int porcentagemAcerto) {
		this.porcentagemAcerto = porcentagemAcerto;
	}

}
