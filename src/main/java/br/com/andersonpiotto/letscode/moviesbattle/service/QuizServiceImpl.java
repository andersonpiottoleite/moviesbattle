package br.com.andersonpiotto.letscode.moviesbattle.service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.andersonpiotto.letscode.moviesbattle.bo.QuizBusiness;
import br.com.andersonpiotto.letscode.moviesbattle.client.FilmeClientImpl;
import br.com.andersonpiotto.letscode.moviesbattle.dto.FilmeAvaliadoDTO;
import br.com.andersonpiotto.letscode.moviesbattle.dto.FilmeDTO;
import br.com.andersonpiotto.letscode.moviesbattle.dto.RespostaQuizRequestDTO;
import br.com.andersonpiotto.letscode.moviesbattle.model.Pergunta;
import br.com.andersonpiotto.letscode.moviesbattle.model.Quiz;
import br.com.andersonpiotto.letscode.moviesbattle.model.Resposta;
import br.com.andersonpiotto.letscode.moviesbattle.model.Usuario;
import br.com.andersonpiotto.letscode.moviesbattle.repository.PerguntaRepository;
import br.com.andersonpiotto.letscode.moviesbattle.repository.QuizRepository;
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
public class QuizServiceImpl implements QuizService {
	
	private static Logger LOGGER = LoggerFactory.getLogger(QuizService.class);
	
	@Autowired
	private QuizBusiness quizBusiness;
	
	@Autowired
	private QuizRepository quizRepository;
	
	@Autowired
	private UsuarioServiceImpl usuarioService;
	
	@Autowired
	private PerguntaRepository perguntaRepository;
	
	@Autowired
	private FilmeClientImpl filmeClient;
	
	/** Construtor usado para testes
	 * 
	 * @param quizBusiness
	 * @param quizRepository
	 * @param usuarioService
	 * @param perguntaRepository
	 * @param filmeClient
	 */
	public QuizServiceImpl(QuizBusiness quizBusiness, QuizRepository quizRepository, UsuarioServiceImpl usuarioService, PerguntaRepository perguntaRepository, FilmeClientImpl filmeClient) {
		this.quizBusiness = quizBusiness;
		this.quizRepository = quizRepository;
		this.usuarioService = usuarioService;
		this.perguntaRepository = perguntaRepository;
		this.filmeClient = filmeClient;
	}

	public QuizServiceImpl() {}

	@Override
	@Transactional
	public QuizResponseVO iniciar(String temaFilme, String token) {
		LOGGER.info("Criando Quiz...");
		
		List<FilmeDTO> filmes = quizBusiness.getFilmesAleatorios(temaFilme);
		FilmeDTO primeiroFilme = filmes.get(0);
		FilmeDTO segundoFilme = filmes.get(1);
		
		Quiz quiz = criaQuiz(primeiroFilme, segundoFilme, token);
		
		quizRepository.save(quiz);
		
		LOGGER.info("Quiz criado com sucesso!");

		return new QuizResponseVO(ResumoFilmeVO.criaResumoFilmeVO(primeiroFilme), ResumoFilmeVO.criaResumoFilmeVO(segundoFilme), quiz.getId(), quiz.getPerguntas().get(0).getId());
	}
	
	private Quiz criaQuiz(FilmeDTO primeiroFilme, FilmeDTO segundoFilme, String token) {
		
		Usuario usuario = usuarioService.buscaPorToken(token);
		
		Quiz quiz = new Quiz(usuario);
		
		adicionaPergunta(primeiroFilme, segundoFilme, quiz);
		
		return quiz;
	}

	private void adicionaPergunta(FilmeDTO primeiroFilme, FilmeDTO segundoFilme, Quiz quiz) {
		LOGGER.info("Adicionando pergunta no Quiz...");
		
		Pergunta pergunta = new Pergunta(primeiroFilme.getImdbID(), segundoFilme.getImdbID());
		quiz.addPergunta(pergunta);
		pergunta.setQuiz(quiz);
		
		LOGGER.info("Pergunta adicionada no Quiz!");
	}
	
	@Override
	@Transactional
	public Quiz responder(RespostaQuizRequestDTO respostaQuizRequest, String token) {
		LOGGER.info("Repondendo pergunta...");
		
		Quiz quiz = quizRepository.findAllByIdAndUsuario_Token(respostaQuizRequest.getIdQuiz(), token).orElseThrow(() -> new IllegalArgumentException("Quiz n??o encontrado para esse id e token"));
		
		Pergunta pergunta = perguntaRepository.findById(respostaQuizRequest.getIdPergunta()).orElseThrow(() -> new IllegalArgumentException("Pergunta n??o encontrada para esse id"));
		
		quizBusiness.validaCondicoesResposta(respostaQuizRequest, quiz, pergunta);
		
		FilmeAvaliadoDTO filmeAvaliado1 = filmeClient.getFilmesPorImdbID(pergunta.getPrimeiraOpcaoImdbID());
		FilmeAvaliadoDTO filmeAvaliado2 = filmeClient.getFilmesPorImdbID(pergunta.getSegundaOpcaoImdbID());
		
		FilmeAvaliadoDTO melhorFilmeAvaliado = quizBusiness.getMelhorAvaliado(filmeAvaliado1, filmeAvaliado2);
		
		Resposta resposta = new Resposta();
		resposta.setImdbIDRespondido(respostaQuizRequest.getImdbIDRespondido());
		
		quizBusiness.verificaRespostaCorreta(quiz, melhorFilmeAvaliado, pergunta, resposta);
		
		Quiz quizRespondido = quizRepository.save(quiz);
		
		LOGGER.info("Pergunta respondida com sucesso!");
		
		return quizRespondido;
		
	}

	@Override
	public QuizResponseVO novaPergunta(Long idQuiz, String temaFilme, String token) {
		LOGGER.info("Criando nova pergunta no Quiz...");
		
		Quiz quiz = quizRepository.findAllByIdAndUsuario_Token(idQuiz, token).orElseThrow(() -> new IllegalArgumentException("Quiz n??o encontrado para esse id e token"));
		
		quizBusiness.validaCondicoesNovaPergunta(quiz);
				
		List<FilmeDTO> filmes = null;
		FilmeDTO primeiroFilme = null;
		FilmeDTO segundoFilme = null;
		
		do {
			filmes = quizBusiness.getFilmesAleatorios(temaFilme);
			primeiroFilme = filmes.get(0);
			segundoFilme = filmes.get(1);
			
		} while(quizBusiness.combinacaoFilmesJaUsada(quiz, primeiroFilme, segundoFilme));
		
		adicionaPergunta(primeiroFilme, segundoFilme, quiz);
		
		quiz = quizRepository.save(quiz);
		
		Pergunta pergunta = quiz.getPerguntas().stream().max(Comparator.comparing(Pergunta::getId))
	      .orElseThrow(NoSuchElementException::new);
		
		LOGGER.info("Nova pergunta criada com sucesso!");
		
		return new QuizResponseVO(ResumoFilmeVO.criaResumoFilmeVO(primeiroFilme), ResumoFilmeVO.criaResumoFilmeVO(segundoFilme), quiz.getId(), pergunta.getId());
	}

	@Override
	public QuizResponseVO getPergunta(Long idPergunta) {
		LOGGER.info("Pesquisando pergunta...");
		
		Pergunta pergunta = perguntaRepository.findById(idPergunta).orElseThrow(() -> new IllegalArgumentException("Pergunta n??o encontrada para esse id"));
		
		FilmeAvaliadoDTO filmeAvaliado1 = filmeClient.getFilmesPorImdbID(pergunta.getPrimeiraOpcaoImdbID());
		FilmeAvaliadoDTO filmeAvaliado2 = filmeClient.getFilmesPorImdbID(pergunta.getSegundaOpcaoImdbID());
		
		LOGGER.info("Pesquisa de pergunta realizada!");
		
		return new QuizResponseVO(ResumoFilmeVO.criaResumoFilmeVO(filmeAvaliado1), ResumoFilmeVO.criaResumoFilmeVO(filmeAvaliado2), pergunta.getQuiz().getId(), pergunta.getId());
	}
	
	@Override
	public Quiz encerrar(Long idQuiz, String token) {
		LOGGER.info("Encerrando Quiz...");
		
		Quiz quiz = quizRepository.findAllByIdAndUsuario_Token(idQuiz, token).orElseThrow(() -> new IllegalArgumentException("Quiz n??o encontrado para esse id e token"));
		quiz.encerra();
		
		Quiz quizEncerrado = quizRepository.save(quiz);
		
		LOGGER.info("Quiz encerrado com sucesso!");
		
		return quizEncerrado;
		
	}

	@Override
	public List<RankeadoVO> getRankeados() {
		return quizBusiness.getRankeados();
	}
}
