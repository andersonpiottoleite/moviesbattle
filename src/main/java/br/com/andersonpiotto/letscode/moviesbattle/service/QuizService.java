package br.com.andersonpiotto.letscode.moviesbattle.service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.andersonpiotto.letscode.moviesbattle.bo.QuizBusinessObject;
import br.com.andersonpiotto.letscode.moviesbattle.client.FilmeClient;
import br.com.andersonpiotto.letscode.moviesbattle.dto.FilmeAvaliadoDTO;
import br.com.andersonpiotto.letscode.moviesbattle.dto.FilmeDTO;
import br.com.andersonpiotto.letscode.moviesbattle.dto.RespostaQuizRequestDTO;
import br.com.andersonpiotto.letscode.moviesbattle.model.Pergunta;
import br.com.andersonpiotto.letscode.moviesbattle.model.Quiz;
import br.com.andersonpiotto.letscode.moviesbattle.model.Resposta;
import br.com.andersonpiotto.letscode.moviesbattle.model.Usuario;
import br.com.andersonpiotto.letscode.moviesbattle.repository.PerguntaRepository;
import br.com.andersonpiotto.letscode.moviesbattle.repository.QuizRepository;
import br.com.andersonpiotto.letscode.moviesbattle.repository.UsuarioRepository;
import br.com.andersonpiotto.letscode.moviesbattle.vo.QuizResponseVO;
import br.com.andersonpiotto.letscode.moviesbattle.vo.RankeadoVO;
import br.com.andersonpiotto.letscode.moviesbattle.vo.ResumoFilmeVO;

/** Classe que representa um service de <code>Quiz</code>
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 19/03/2022
 */
@Service
public class QuizService {
	
	@Autowired
	private QuizBusinessObject quizBusinessObject;
	
	@Autowired
	private QuizRepository quizRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private PerguntaRepository perguntaRepository;
	
	@Autowired
	private FilmeClient filmeClient;

	public QuizResponseVO iniciar(String temaFilme) {
		
		List<FilmeDTO> filmes = quizBusinessObject.getFilmesAleatorios(temaFilme);
		FilmeDTO primeiroFilme = filmes.get(0);
		FilmeDTO segundoFilme = filmes.get(1);
		
		Quiz quiz = criaQuiz(primeiroFilme, segundoFilme);
		
		quizRepository.save(quiz);

		return new QuizResponseVO(ResumoFilmeVO.criaResumoFilmeVO(primeiroFilme), ResumoFilmeVO.criaResumoFilmeVO(segundoFilme), quiz.getId(), quiz.getPerguntas().get(0).getId());
	}
	
	private Quiz criaQuiz(FilmeDTO primeiroFilme, FilmeDTO segundoFilme) {
		// TODO pegar ususario da sessao
		Usuario usuario = new Usuario("login", "senha", "Anderson Piotto");
		
		Quiz quiz = new Quiz(usuario);
		
		adicionaPergunta(primeiroFilme, segundoFilme, quiz);
		
		return quiz;
	}

	private void adicionaPergunta(FilmeDTO primeiroFilme, FilmeDTO segundoFilme, Quiz quiz) {
		Pergunta pergunta = new Pergunta(primeiroFilme.getImdbID(), segundoFilme.getImdbID());
		quiz.addPergunta(pergunta);
		pergunta.setQuiz(quiz);
	}
	
	@Transactional
	public void responder(RespostaQuizRequestDTO respostaQuizRequest) {
		Quiz quiz = quizRepository.findById(respostaQuizRequest.getIdQuiz()).orElseThrow(() -> new IllegalArgumentException("Quiz não encontrado"));
		
		Pergunta pergunta = perguntaRepository.findById(respostaQuizRequest.getIdPergunta()).orElseThrow(() -> new IllegalArgumentException("Pergunta não encontrada"));
		
		quizBusinessObject.validaCondicoesResposta(respostaQuizRequest, quiz, pergunta);
		
		FilmeAvaliadoDTO filmeAvaliado1 = filmeClient.getFilmesPorImdbID(pergunta.getPrimeiraOpcaoImdbID());
		FilmeAvaliadoDTO filmeAvaliado2 = filmeClient.getFilmesPorImdbID(pergunta.getSegundaOpcaoImdbID());
		
		FilmeAvaliadoDTO melhorFilmeAvaliado = quizBusinessObject.getMelhorAvaliado(filmeAvaliado1, filmeAvaliado2);
		
		Resposta resposta = new Resposta();
		resposta.setImdbIDRespondido(respostaQuizRequest.getImdbIDRespondido());
		
		quizBusinessObject.verificaRespostaCorreta(quiz, melhorFilmeAvaliado, pergunta, resposta);
		
		quizRepository.save(quiz);
		
	}

	public QuizResponseVO novaPergunta(Long idQuiz, String temaFilme) {
		Quiz quiz = quizRepository.findById(idQuiz).orElseThrow(() -> new IllegalArgumentException("Quiz não encontrado"));
		
		quizBusinessObject.existePerguntaNaoRespondida(quiz);
				
		List<FilmeDTO> filmes = null;
		FilmeDTO primeiroFilme = null;
		FilmeDTO segundoFilme = null;
		
		do {
			filmes = quizBusinessObject.getFilmesAleatorios(temaFilme);
			primeiroFilme = filmes.get(0);
			segundoFilme = filmes.get(1);
			
		} while(quizBusinessObject.combinacaoFilmesJaUsada(quiz, primeiroFilme, segundoFilme));
		
		adicionaPergunta(primeiroFilme, segundoFilme, quiz);
		
		quiz = quizRepository.save(quiz);
		
		Pergunta pergunta = quiz.getPerguntas().stream().max(Comparator.comparing(Pergunta::getId))
	      .orElseThrow(NoSuchElementException::new);
		
		return new QuizResponseVO(ResumoFilmeVO.criaResumoFilmeVO(primeiroFilme), ResumoFilmeVO.criaResumoFilmeVO(segundoFilme), quiz.getId(), pergunta.getId());
	}

	public QuizResponseVO getPergunta(Long idPergunta) {
		Pergunta pergunta = perguntaRepository.findById(idPergunta).orElseThrow(() -> new IllegalArgumentException("Pergunta não encontrada"));
		
		FilmeAvaliadoDTO filmeAvaliado1 = filmeClient.getFilmesPorImdbID(pergunta.getPrimeiraOpcaoImdbID());
		FilmeAvaliadoDTO filmeAvaliado2 = filmeClient.getFilmesPorImdbID(pergunta.getSegundaOpcaoImdbID());
		
		return new QuizResponseVO(ResumoFilmeVO.criaResumoFilmeVO(filmeAvaliado1), ResumoFilmeVO.criaResumoFilmeVO(filmeAvaliado2), pergunta.getQuiz().getId(), pergunta.getId());
	}
	
	public void encerrar(Long idQuiz) {
		Quiz quiz = quizRepository.findById(idQuiz).orElseThrow(() -> new IllegalArgumentException("Quiz não encontrado"));
		quiz.encerra();
		quizRepository.save(quiz);
	}

	public List<RankeadoVO> getRankeados() {
		return quizBusinessObject.getRankeados();
	}
}
