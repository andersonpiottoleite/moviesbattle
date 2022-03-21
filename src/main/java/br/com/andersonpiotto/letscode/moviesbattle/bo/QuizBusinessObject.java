package br.com.andersonpiotto.letscode.moviesbattle.bo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.andersonpiotto.letscode.moviesbattle.client.FilmeClientImpl;
import br.com.andersonpiotto.letscode.moviesbattle.dto.FilmeAvaliadoDTO;
import br.com.andersonpiotto.letscode.moviesbattle.dto.FilmeDTO;
import br.com.andersonpiotto.letscode.moviesbattle.dto.ListFilmesDTO;
import br.com.andersonpiotto.letscode.moviesbattle.dto.RespostaQuizRequestDTO;
import br.com.andersonpiotto.letscode.moviesbattle.model.Pergunta;
import br.com.andersonpiotto.letscode.moviesbattle.model.Quiz;
import br.com.andersonpiotto.letscode.moviesbattle.model.Resposta;
import br.com.andersonpiotto.letscode.moviesbattle.model.Usuario;
import br.com.andersonpiotto.letscode.moviesbattle.repository.QuizRepository;
import br.com.andersonpiotto.letscode.moviesbattle.repository.UsuarioRepository;
import br.com.andersonpiotto.letscode.moviesbattle.vo.RankeadoVO;

/** Classe que encapsula as regras de negócio do quiz
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 20/03/2022
 */

@Component
public class QuizBusinessObject {
	
	private static Logger LOGGER = LoggerFactory.getLogger(QuizBusinessObject.class);
	
	@Autowired
	private FilmeClientImpl filmeClient;
	
	@Autowired
	private QuizRepository quizRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	public List<FilmeDTO> getFilmesAleatorios(String temaFilme) {
		ListFilmesDTO filmes = filmeClient.getFilmes(temaFilme);
		
		final int quantidadeMaximaFilmes = filmes.getFilmes().size() -1;
		int primeiroNumeroAleatorio = geraNumeroAleatorio(quantidadeMaximaFilmes);
		int segundoNumeroAleatorio = 0;
		
		do {			
			segundoNumeroAleatorio = geraNumeroAleatorio(quantidadeMaximaFilmes);
		} while (primeiroNumeroAleatorio == segundoNumeroAleatorio);
		
		FilmeDTO primeiroFilme = getFilme(filmes, primeiroNumeroAleatorio);
		FilmeDTO segundoFilme = getFilme(filmes, segundoNumeroAleatorio);
		
		return Arrays.asList(primeiroFilme, segundoFilme);
	}
	
	private int geraNumeroAleatorio(int limite) {
		return new Random().nextInt(limite);
	}
	
	private FilmeDTO getFilme(ListFilmesDTO filmes, int numeroAleatorio) {
		return filmes.getFilmes().get(numeroAleatorio);
	}
	
	public boolean combinacaoFilmesJaUsada(Quiz quiz, FilmeDTO primeiroFilme, FilmeDTO segundoFilme) {
		return quiz.getPerguntas().stream().filter(p -> 
				(p.getPrimeiraOpcaoImdbID().equals(primeiroFilme.getImdbID()) && p.getSegundaOpcaoImdbID().equals(segundoFilme.getImdbID())) || 
				(p.getPrimeiraOpcaoImdbID().equals(segundoFilme.getImdbID()) && p.getSegundaOpcaoImdbID().equals(primeiroFilme.getImdbID()))
		).findAny().isPresent();
	}
	

	public void validaCondicoesResposta(RespostaQuizRequestDTO respostaQuizRequest, Quiz quiz, Pergunta pergunta) {
		validaQuizEncerrado(quiz);
		
		if (pergunta.isRespondida()) {
			throw new IllegalArgumentException("Pergunta já respondida, crie uma nova pergunta!");
		}
		
		if (!respostaQuizRequest.getImdbIDRespondido().equals(pergunta.getPrimeiraOpcaoImdbID()) &&
				!respostaQuizRequest.getImdbIDRespondido().equals(pergunta.getSegundaOpcaoImdbID())) {
			
			throw new IllegalArgumentException("Opção não encontrada para a pergunta, informe um Filme que tenha um dos ImdbID's: "
				+ pergunta.getPrimeiraOpcaoImdbID() + " ou " +  pergunta.getSegundaOpcaoImdbID());
		}
	}
	
	public void validaCondicoesNovaPergunta(Quiz quiz) {
		validaQuizEncerrado(quiz);
		existePerguntaNaoRespondida(quiz);
	}
	
	private void validaQuizEncerrado(Quiz quiz) {
		if (quiz.isEncerrado()) {
			throw new IllegalArgumentException("Quiz Encerrado, abra um novo quiz!");
		}
	}
	
	private void existePerguntaNaoRespondida(Quiz quiz) {
		Optional<Pergunta> optional = quiz.getPerguntas().stream().filter( p -> Objects.isNull(p.getResposta())).findAny();
		if (! optional.isEmpty()) {
			throw new IllegalStateException("Existe uma Pergunta pendente de resposta! Responda a Pergunta: " + optional.get().getId());
		}
	}
	
	public FilmeAvaliadoDTO getMelhorAvaliado(FilmeAvaliadoDTO filmeAvaliado1, FilmeAvaliadoDTO filmeAvaliado2) {
		double avaliacao1 = 0;
		double avaliacao2 = 0;
		
		try {
			avaliacao1 = Double.parseDouble(filmeAvaliado1.getImdbRating());
		} catch (Exception e) {
			// pode vir "N/A" como avaliacao do IMBD
			LOGGER.error("Erro ao converter " + filmeAvaliado1.getImdbRating() + " para double");
		}
		
		try {
			avaliacao2 = Double.parseDouble(filmeAvaliado2.getImdbRating());
		} catch (Exception e) {
			// pode vir "N/A" como avaliacao do IMBD
			LOGGER.error("Erro ao converter " + filmeAvaliado2.getImdbRating() + " para double");
		}
		
		return avaliacao1 > avaliacao2 ? filmeAvaliado1 : filmeAvaliado2;
	}
	
	public void verificaRespostaCorreta(Quiz quiz, FilmeAvaliadoDTO melhorFilmeAvaliado, Pergunta pergunta, Resposta resposta) {
		if (resposta.getImdbIDRespondido().equals(melhorFilmeAvaliado.getImdbID())) {
			resposta.setCorreta(true);
			quiz.setQuantidadeRespostasCorretas(quiz.getQuantidadeRespostasCorretas() + 1);
			
		} else {
			resposta.setCorreta(false);
			quiz.setQuantidadeErros(quiz.getQuantidadeErros() + 1);
			if (quiz.getQuantidadeErros() == 3) {
				quiz.encerra();
			}
		}
		
		pergunta.setResposta(resposta);
		pergunta.respondida();
	}
	
	public List<RankeadoVO> getRankeados() {
		LOGGER.info("Gerando Ranking do Quiz...");
		
		List<RankeadoVO> rankeados = new ArrayList<RankeadoVO>();
		
		Iterator<Usuario> iterator = usuarioRepository.findAll().iterator();
		
		if (!iterator.hasNext()) {
			throw new IllegalStateException("Não existem participantes para rankear!");
		}
		
		preencheDadosBasicosRankeados(rankeados, iterator);
		
		ordenaRakeados(rankeados);
		
		setNumeroPosicaoRanking(rankeados);
		
		LOGGER.info("Ranking do Quiz Gerado!");
		
		return rankeados;
	}

	private void preencheDadosBasicosRankeados(List<RankeadoVO> rankeados, Iterator<Usuario> iterator) {
		List<Quiz> quizes;
		
		while (iterator.hasNext()) {
			 Usuario usuario = iterator.next();
			
			 quizes = quizRepository.findAllByUsuario(usuario);
			 
			 RankeadoVO rankeado = new RankeadoVO();
			 
           	 rankeado.setNome(usuario.getUsername());
           	 
           	 if (!quizes.isEmpty()) {
           		 
				 int quantidadeQuizes = quizes.size();
				 rankeado.setQuantidadeDeQuizesRespondidos(quantidadeQuizes);
				 
				 int totalQuantidadeRespostasCertas = quizes.stream()
				 .mapToInt(q -> q.getQuantidadeRespostasCorretas())
				 .sum();
				 rankeado.setQuantidadeTotalRepostasCertas(totalQuantidadeRespostasCertas);
				 
				 int totalRespostas = 0;
				 for (Quiz quiz : quizes) {
					 totalRespostas += quiz.getPerguntas().stream().filter(p -> p.isRespondida()).count();
				 }
				 
				 rankeado.setQuantidadeTotalRespostas(totalRespostas);
				 rankeado.setQuantidadeTotalRepostasErradas(totalRespostas - totalQuantidadeRespostasCertas);
				 
				 int porcentagemAcerto = 0;
				 if (totalRespostas > 0) {
					 porcentagemAcerto = (totalQuantidadeRespostasCertas * 100) / totalRespostas;
				 }
				 
				 rankeado.setPorcentagemAcerto(porcentagemAcerto);
				 
				 rankeado.setPontuacao(quantidadeQuizes * porcentagemAcerto);
           	 }
			 
			 rankeados.add(rankeado);
		}
	}

	private void ordenaRakeados(List<RankeadoVO> rankeados) {
		Collections.sort(rankeados,new Comparator<RankeadoVO>(){
			
            public int compare(RankeadoVO r1,RankeadoVO r2){
            	if(r1.getPontuacao() > r2.getPontuacao()) {
            		return -1;
            	}else if(r1.getPontuacao() < r2.getPontuacao()) {
            		return 1;
            	}
            	
            	return 0;
        }});
	}
	
	private void setNumeroPosicaoRanking(List<RankeadoVO> rankeados) {
		int posicaoRanking = 1;
		
		for (RankeadoVO rankeadoVO : rankeados) {			
			rankeadoVO.setPosicaoRanking(posicaoRanking ++);
		}
	}
}
