package br.com.andersonpiotto.letscode.moviesbattle.bo;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import br.com.andersonpiotto.letscode.moviesbattle.client.FilmeClientImpl;
import br.com.andersonpiotto.letscode.moviesbattle.dto.FilmeAvaliadoDTO;
import br.com.andersonpiotto.letscode.moviesbattle.dto.FilmeDTO;
import br.com.andersonpiotto.letscode.moviesbattle.dto.RespostaQuizRequestDTO;
import br.com.andersonpiotto.letscode.moviesbattle.dto.UsuarioDTO;
import br.com.andersonpiotto.letscode.moviesbattle.model.Pergunta;
import br.com.andersonpiotto.letscode.moviesbattle.model.Quiz;
import br.com.andersonpiotto.letscode.moviesbattle.model.Resposta;
import br.com.andersonpiotto.letscode.moviesbattle.repository.PerguntaRepository;
import br.com.andersonpiotto.letscode.moviesbattle.repository.QuizRepository;
import br.com.andersonpiotto.letscode.moviesbattle.repository.UsuarioRepository;
import br.com.andersonpiotto.letscode.moviesbattle.service.QuizServiceImpl;
import br.com.andersonpiotto.letscode.moviesbattle.service.UsuarioServiceImpl;
import br.com.andersonpiotto.letscode.moviesbattle.vo.QuizResponseVO;
import br.com.andersonpiotto.letscode.moviesbattle.vo.RankeadoVO;
import br.com.andersonpiotto.letscode.moviesbattle.vo.ResumoFilmeVO;
import br.com.andersonpiotto.letscode.moviesbattle.vo.UsuarioVO;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de testes para <code>QuizBusiness</code>
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 21/03/2022
 */

@DataJpaTest
@ActiveProfiles("test")
class QuizBusinessTest {


	@Autowired
	private QuizRepository quizRepository;

	@Autowired
	private PerguntaRepository perguntaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	private QuizServiceImpl quizService;

	private UsuarioServiceImpl usuarioService;

	private QuizBusiness quizBusiness;

	private FilmeClientImpl filmeClient;

	@BeforeEach
	void antesDeCadaTeste() {
		// TODO para deixar o teste caracterizado com teste de unidade (teste unitario), para essa classe Business Test
		// pode ser usado a biblioteca do mockito, para relaizar mocks dos metodos dos repositorys e seus valores retornados,
		// assim não será necessário à base de dados.
		
		filmeClient = new FilmeClientImpl(new RestTemplate());
		quizBusiness = new QuizBusiness(filmeClient, quizRepository, usuarioRepository);
		usuarioService = new UsuarioServiceImpl(usuarioRepository);
		quizService = new QuizServiceImpl(quizBusiness, quizRepository, usuarioService, perguntaRepository, filmeClient);
	}

	@Test
	void deveRetornarDoisfilmesDiferentesDoServicoDaIMBD() {
		String temaFilme = "Amor";

		List<FilmeDTO> filmesAleatorios = quizBusiness.getFilmesAleatorios(temaFilme);

		assertNotNull(filmesAleatorios);
		assertTrue(filmesAleatorios.size() == 2);
		assertNotEquals(filmesAleatorios.get(0).getImdbID(), filmesAleatorios.get(1).getImdbID());

	}

	@Test
	void deveValidarSeCombinacaoDeFilmesJaFoiUsadaNoQuiz() {
		QuizResponseVO quizResponseVO = criaQuiz();
		
		ResumoFilmeVO primeiroFilme = quizResponseVO.getPrimeiroFilme();
		ResumoFilmeVO segundoFilme = quizResponseVO.getSegundoFilme();
		Quiz quiz = quizRepository.findById(quizResponseVO.getIdQuiz()).get();
		
		FilmeDTO filmeDTO1 = new FilmeDTO();
		filmeDTO1.setImdbID(primeiroFilme.getImdbID());
		
		FilmeDTO filmeDTO2 = new FilmeDTO();
		filmeDTO2.setImdbID(segundoFilme.getImdbID());
		
		boolean combinacaoFilmesJaUsada = quizBusiness.combinacaoFilmesJaUsada(quiz, filmeDTO1, filmeDTO2);

		assertTrue(combinacaoFilmesJaUsada);
		
		filmeDTO2.setImdbID("outro ImdbID qualquer");
		combinacaoFilmesJaUsada = quizBusiness.combinacaoFilmesJaUsada(quiz, filmeDTO1, filmeDTO2);

		assertFalse(combinacaoFilmesJaUsada);

	}
	
	@Test
	void naoDevePermitirRespostaParaPerguntaJaRepondida() {
		UsuarioVO usuario = criaUsuario();
		
		QuizResponseVO quizResponseVO = criaQuiz(usuario);
		
		RespostaQuizRequestDTO respostaQuizRequestDTO = new RespostaQuizRequestDTO();
		respostaQuizRequestDTO.setIdQuiz(quizResponseVO.getIdQuiz());
		respostaQuizRequestDTO.setIdPergunta(quizResponseVO.getIdPergunta());
		respostaQuizRequestDTO.setImdbIDRespondido(quizResponseVO.getPrimeiroFilme().getImdbID());
		
		quizService.responder(respostaQuizRequestDTO, usuario.getToken());
		
		// tentando responder a mesma pergunta
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			respostaQuizRequestDTO.setIdQuiz(quizResponseVO.getIdQuiz());
			respostaQuizRequestDTO.setIdPergunta(quizResponseVO.getIdPergunta());
			respostaQuizRequestDTO.setImdbIDRespondido(quizResponseVO.getPrimeiroFilme().getImdbID());
			
			Quiz quiz = quizRepository.findById(quizResponseVO.getIdQuiz()).get();
			quizBusiness.validaCondicoesResposta(respostaQuizRequestDTO, quiz, quiz.getPerguntas().get(0));
		
		});

		String expectedMessage = "Pergunta já respondida, crie uma nova pergunta!";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void naoDevePermitirRespostaParaQuizJaEncerrado() {
		UsuarioVO usuario = criaUsuario();
		
		QuizResponseVO quizResponseVO = criaQuiz(usuario);
		
		quizService.encerrar(quizResponseVO.getIdQuiz(), usuario.getToken());
		
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			RespostaQuizRequestDTO respostaQuizRequestDTO = new RespostaQuizRequestDTO();
			respostaQuizRequestDTO.setIdQuiz(quizResponseVO.getIdQuiz());
			respostaQuizRequestDTO.setIdPergunta(quizResponseVO.getIdPergunta());
			respostaQuizRequestDTO.setImdbIDRespondido(quizResponseVO.getPrimeiroFilme().getImdbID());
				
			Quiz quiz = quizRepository.findById(quizResponseVO.getIdQuiz()).get();
			quizBusiness.validaCondicoesResposta(respostaQuizRequestDTO, quiz, quiz.getPerguntas().get(0));
		
		});

		String expectedMessage = "Quiz Encerrado, abra um novo quiz!";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void naoDevePermitirRespostaComImdbIDInexistente() {
		UsuarioVO usuario = criaUsuario();
		
		QuizResponseVO quizResponseVO = criaQuiz(usuario);
		
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			RespostaQuizRequestDTO respostaQuizRequestDTO = new RespostaQuizRequestDTO();
			respostaQuizRequestDTO.setIdQuiz(quizResponseVO.getIdQuiz());
			respostaQuizRequestDTO.setIdPergunta(quizResponseVO.getIdPergunta());
			respostaQuizRequestDTO.setImdbIDRespondido("um ImdbID inexistente");
				
			Quiz quiz = quizRepository.findById(quizResponseVO.getIdQuiz()).get();
			quizBusiness.validaCondicoesResposta(respostaQuizRequestDTO, quiz, quiz.getPerguntas().get(0));
		
		});

		String expectedMessage = "Opção não encontrada para a pergunta, informe um Filme que tenha um dos ImdbID's: ";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void naoDevePermitirCriacaoDeNovaPerguntaParaQuizJaEncerrado() {
		UsuarioVO usuario = criaUsuario();
		
		QuizResponseVO quizResponseVO = criaQuiz(usuario);
		
		RespostaQuizRequestDTO respostaQuizRequestDTO = new RespostaQuizRequestDTO();
		respostaQuizRequestDTO.setIdQuiz(quizResponseVO.getIdQuiz());
		respostaQuizRequestDTO.setIdPergunta(quizResponseVO.getIdPergunta());
		respostaQuizRequestDTO.setImdbIDRespondido(quizResponseVO.getPrimeiroFilme().getImdbID());
			
		quizService.responder(respostaQuizRequestDTO, usuario.getToken());
		
		quizService.encerrar(quizResponseVO.getIdQuiz(), usuario.getToken());
		
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			Quiz quiz = quizRepository.findById(quizResponseVO.getIdQuiz()).get();
			quizBusiness.validaCondicoesNovaPergunta(quiz);
		});

		String expectedMessage = "Quiz Encerrado, abra um novo quiz!";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void naoDevePermitirCriacaoDeNovaPerguntaSEexistePerguntaNaoRespondida() {
		UsuarioVO usuario = criaUsuario();
		
		QuizResponseVO quizResponseVO = criaQuiz(usuario);
		
		RespostaQuizRequestDTO respostaQuizRequestDTO = new RespostaQuizRequestDTO();
		respostaQuizRequestDTO.setIdQuiz(quizResponseVO.getIdQuiz());
		respostaQuizRequestDTO.setIdPergunta(quizResponseVO.getIdPergunta());
		respostaQuizRequestDTO.setImdbIDRespondido(quizResponseVO.getPrimeiroFilme().getImdbID());
		
		Exception exception = assertThrows(IllegalStateException.class, () -> {
			Quiz quiz = quizRepository.findById(quizResponseVO.getIdQuiz()).get();
			quizBusiness.validaCondicoesNovaPergunta(quiz);
		});

		String expectedMessage = "Existe uma Pergunta pendente de resposta! Responda a Pergunta: ";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void deveRetornarMelhorFilmeAvaliado() {
		UsuarioVO usuario = criaUsuario();
		
		QuizResponseVO quizResponseVO = criaQuiz(usuario);
		
		FilmeAvaliadoDTO filmeAvaliado1 = filmeClient.getFilmesPorImdbID(quizResponseVO.getPrimeiroFilme().getImdbID());
		FilmeAvaliadoDTO filmeAvaliado2 = filmeClient.getFilmesPorImdbID(quizResponseVO.getSegundoFilme().getImdbID());
		
		// mudando avalidacao somente para testes
		filmeAvaliado1.setImdbRating("9.9");
		filmeAvaliado2.setImdbRating("0.0");
		
		FilmeAvaliadoDTO melhorAvaliado = quizBusiness.getMelhorAvaliado(filmeAvaliado1, filmeAvaliado2);
		
		assertEquals(filmeAvaliado1.getImdbID(), melhorAvaliado.getImdbID());
	}
	
	@Test
	void validaSeRespostaCorretaFoiConfiguradaAdequadaqmente() {
		UsuarioVO usuario = criaUsuario();
		
		QuizResponseVO quizResponseVO = criaQuiz(usuario);
		
		FilmeAvaliadoDTO filmeAvaliado1 = filmeClient.getFilmesPorImdbID(quizResponseVO.getPrimeiroFilme().getImdbID());
		FilmeAvaliadoDTO filmeAvaliado2 = filmeClient.getFilmesPorImdbID(quizResponseVO.getSegundoFilme().getImdbID());
		
		// mudando avalidacao somente para testes
		filmeAvaliado1.setImdbRating("9.9");
		filmeAvaliado2.setImdbRating("0.0");
		
		FilmeAvaliadoDTO melhorAvaliado = quizBusiness.getMelhorAvaliado(filmeAvaliado1, filmeAvaliado2);
		
		RespostaQuizRequestDTO respostaQuizRequestDTO = new RespostaQuizRequestDTO();
		respostaQuizRequestDTO.setIdQuiz(quizResponseVO.getIdQuiz());
		respostaQuizRequestDTO.setIdPergunta(quizResponseVO.getIdPergunta());
		respostaQuizRequestDTO.setImdbIDRespondido(quizResponseVO.getPrimeiroFilme().getImdbID());
			
		Quiz quiz = quizRepository.findById(quizResponseVO.getIdQuiz()).get();
		
		Pergunta pergunta = quiz.getPerguntas().get(0);
		
		Resposta resposta = new Resposta();
		resposta.setImdbIDRespondido(respostaQuizRequestDTO.getImdbIDRespondido());
		
		quizBusiness.verificaRespostaCorreta(quiz, melhorAvaliado, pergunta, resposta);
		
		assertTrue(pergunta.isRespondida());
		assertTrue(pergunta.getResposta().getCorreta());
		assertTrue(quiz.getQuantidadeRespostasCorretas() == 1);
		assertTrue(quiz.getQuantidadeErros() == 0);
	}
	
	@Test
	void validaSeRespostaIncorretaFoiConfiguradaAdequadaqmente() {
		UsuarioVO usuario = criaUsuario();
		
		QuizResponseVO quizResponseVO = criaQuiz(usuario);
		
		FilmeAvaliadoDTO filmeAvaliado1 = filmeClient.getFilmesPorImdbID(quizResponseVO.getPrimeiroFilme().getImdbID());
		FilmeAvaliadoDTO filmeAvaliado2 = filmeClient.getFilmesPorImdbID(quizResponseVO.getSegundoFilme().getImdbID());
		
		// mudando avalidacao somente para testes
		filmeAvaliado1.setImdbRating("0.0");
		filmeAvaliado2.setImdbRating("9.9");
		
		FilmeAvaliadoDTO melhorAvaliado = quizBusiness.getMelhorAvaliado(filmeAvaliado1, filmeAvaliado2);
		
		RespostaQuizRequestDTO respostaQuizRequestDTO = new RespostaQuizRequestDTO();
		respostaQuizRequestDTO.setIdQuiz(quizResponseVO.getIdQuiz());
		respostaQuizRequestDTO.setIdPergunta(quizResponseVO.getIdPergunta());
		respostaQuizRequestDTO.setImdbIDRespondido(quizResponseVO.getPrimeiroFilme().getImdbID());
			
		Quiz quiz = quizRepository.findById(quizResponseVO.getIdQuiz()).get();
		
		Pergunta pergunta = quiz.getPerguntas().get(0);
		
		Resposta resposta = new Resposta();
		resposta.setImdbIDRespondido(respostaQuizRequestDTO.getImdbIDRespondido());
		
		quizBusiness.verificaRespostaCorreta(quiz, melhorAvaliado, pergunta, resposta);
		
		assertTrue(pergunta.isRespondida());
		assertFalse(pergunta.getResposta().getCorreta());
		assertTrue(quiz.getQuantidadeRespostasCorretas() == 0);
		assertTrue(quiz.getQuantidadeErros() == 1);
	}
	

	@Test
	void deveBuscarRanqueados() {
		
		// Primeiro usuario, com um quiz e com duas perguntas respondidas
		UsuarioVO usuario = criaUsuario();
		
		QuizResponseVO quizResponseVO = criaQuiz(usuario);
		
		RespostaQuizRequestDTO respostaQuizRequestDTO = new RespostaQuizRequestDTO();
		respostaQuizRequestDTO.setIdQuiz(quizResponseVO.getIdQuiz());
		respostaQuizRequestDTO.setIdPergunta(quizResponseVO.getIdPergunta());
		respostaQuizRequestDTO.setImdbIDRespondido(quizResponseVO.getPrimeiroFilme().getImdbID());
		
		quizService.responder(respostaQuizRequestDTO, usuario.getToken());
		
		String temaFilme = "Amor";
		
		QuizResponseVO quizResponseVOComNovaPergunta = quizService.novaPergunta(quizResponseVO.getIdQuiz(), temaFilme, usuario.getToken());
		
		assertNotNull(quizResponseVOComNovaPergunta.getIdPergunta());
		
		RespostaQuizRequestDTO respostaQuizRequestDTO2 = new RespostaQuizRequestDTO();
		respostaQuizRequestDTO2.setIdQuiz(quizResponseVOComNovaPergunta.getIdQuiz());
		respostaQuizRequestDTO2.setIdPergunta(quizResponseVOComNovaPergunta.getIdPergunta());
		respostaQuizRequestDTO2.setImdbIDRespondido(quizResponseVOComNovaPergunta.getPrimeiroFilme().getImdbID());
		
		quizService.responder(respostaQuizRequestDTO2, usuario.getToken());
		
		
		// Segundo usuario, com um quiz e com uma pergunta respondida
		UsuarioVO usuario2 = criaUsuario();
		
		QuizResponseVO quizResponseVOUser2 = criaQuiz(usuario2);
		
		RespostaQuizRequestDTO respostaQuizRequestDTOUser2 = new RespostaQuizRequestDTO();
		respostaQuizRequestDTOUser2.setIdQuiz(quizResponseVOUser2.getIdQuiz());
		respostaQuizRequestDTOUser2.setIdPergunta(quizResponseVOUser2.getIdPergunta());
		respostaQuizRequestDTOUser2.setImdbIDRespondido(quizResponseVOUser2.getPrimeiroFilme().getImdbID());
		
		quizService.responder(respostaQuizRequestDTOUser2, usuario2.getToken());
		
		// obterndo ranking
		List<RankeadoVO> rankeados = quizBusiness.getRankeados();
		
		assertNotNull(rankeados);
		assertTrue(rankeados.size() == 2);
		assertTrue(rankeados.get(0).getPosicaoRanking() != rankeados.get(1).getPosicaoRanking());
	}

	private QuizResponseVO criaQuiz(UsuarioVO usuario) {
		String temaFilme = "Amor";
		String token = usuario.getToken();
		QuizResponseVO quizResponseVO = quizService.iniciar(temaFilme, token);
		return quizResponseVO;
	}
	
	private QuizResponseVO criaQuiz() {
		UsuarioVO usuario = criaUsuario();
		
		String temaFilme = "Amor";
		String token = usuario.getToken();
		QuizResponseVO quizResponseVO = quizService.iniciar(temaFilme, token);
		return quizResponseVO;
	}
	
	private UsuarioVO criaUsuario() {
		UsuarioDTO usuarioDTO = new UsuarioDTO();
		usuarioDTO.setUsername("Anderson Piotto");
		UsuarioVO usuarioCriado = usuarioService.cria(usuarioDTO);
		return usuarioCriado;
	}

}