package br.com.andersonpiotto.letscode.moviesbattle.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.web.client.RestTemplate;

import br.com.andersonpiotto.letscode.moviesbattle.bo.QuizBusinessObject;
import br.com.andersonpiotto.letscode.moviesbattle.client.FilmeClientImpl;
import br.com.andersonpiotto.letscode.moviesbattle.dto.RespostaQuizRequestDTO;
import br.com.andersonpiotto.letscode.moviesbattle.dto.UsuarioDTO;
import br.com.andersonpiotto.letscode.moviesbattle.model.Quiz;
import br.com.andersonpiotto.letscode.moviesbattle.repository.PerguntaRepository;
import br.com.andersonpiotto.letscode.moviesbattle.repository.QuizRepository;
import br.com.andersonpiotto.letscode.moviesbattle.repository.UsuarioRepository;
import br.com.andersonpiotto.letscode.moviesbattle.vo.QuizResponseVO;
import br.com.andersonpiotto.letscode.moviesbattle.vo.RankeadoVO;
import br.com.andersonpiotto.letscode.moviesbattle.vo.UsuarioVO;


/**
 * Classe de testes para <code>QuizServiceImpl</code>
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 21/03/2022
 */

@DataJpaTest
class QuizServiceImplTest {
	
	private QuizServiceImpl quizService;
	
	@Autowired
	private QuizRepository quizRepository;
	
	private QuizBusinessObject quizBusinessObject;
	
	private FilmeClientImpl filmeClient;
	
	@Autowired
	private PerguntaRepository perguntaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	private UsuarioServiceImpl usuarioService;

	@BeforeEach
	void antesDeCadaTeste() {
		usuarioService = new UsuarioServiceImpl(usuarioRepository);
		
		filmeClient = new FilmeClientImpl(new RestTemplate());
		quizBusinessObject = new QuizBusinessObject(filmeClient, quizRepository, usuarioRepository);
		quizService = new QuizServiceImpl(quizBusinessObject, quizRepository, usuarioService, perguntaRepository, filmeClient);
	}

	@Test
	void deveIniciarUmQuiz() {
		
		QuizResponseVO quizResponseVO = criaQuiz();
		
		assertNotNull(quizResponseVO);
		
		assertNotNull(quizResponseVO.getIdQuiz());
		assertNotNull(quizResponseVO.getIdPergunta());
		
		assertNotNull(quizResponseVO.getPrimeiroFilme());
		assertNotNull(quizResponseVO.getPrimeiroFilme().getImdbID());

		assertNotNull(quizResponseVO.getSegundoFilme());
		assertNotNull(quizResponseVO.getSegundoFilme().getImdbID());
	}
	
	@Test
	void deveResponderUmaPergunta() {
		
		UsuarioVO usuario = criaUsuario();
		
		QuizResponseVO quizResponseVO = criaQuiz(usuario);
		
		RespostaQuizRequestDTO respostaQuizRequestDTO = new RespostaQuizRequestDTO();
		respostaQuizRequestDTO.setIdQuiz(quizResponseVO.getIdQuiz());
		respostaQuizRequestDTO.setIdPergunta(quizResponseVO.getIdPergunta());
		respostaQuizRequestDTO.setImdbIDRespondido(quizResponseVO.getPrimeiroFilme().getImdbID());
		
		Quiz quiz = quizService.responder(respostaQuizRequestDTO, usuario.getToken());
		
		assertNotNull(quiz.getId());
		
		boolean perguntaSemResposta = quiz.getPerguntas().stream().filter(p -> p.getResposta() == null).findAny().isPresent();
		boolean perguntaNaoRespondida = quiz.getPerguntas().stream().filter(p -> ! p.isRespondida()).findAny().isPresent();
		
		assertFalse(perguntaSemResposta);
		assertFalse(perguntaNaoRespondida);
	}
	
	@Test
	void naoDeveCriarUmaNovaPerguntaSeHouverPerguntaNaoRespondida() {
		
		Exception exception = assertThrows(IllegalStateException.class, () -> {
			UsuarioVO usuario = criaUsuario();
			
			QuizResponseVO quizResponseVO = criaQuiz(usuario);
			
			String temaFilme = "Amor";
			
			quizService.novaPergunta(quizResponseVO.getIdQuiz(), temaFilme, usuario.getToken());
		});

		String expectedMessage = "Existe uma Pergunta pendente de resposta! Responda a Pergunta";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void naoDeveCriarUmaNovaPerguntaSeQuizEncerrado() {
		
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			UsuarioVO usuario = criaUsuario();
			
			QuizResponseVO quizResponseVO = criaQuiz(usuario);
			
			quizService.encerrar(quizResponseVO.getIdQuiz(), usuario.getToken());
			
			String temaFilme = "Amor";
			
			quizService.novaPergunta(quizResponseVO.getIdQuiz(), temaFilme, usuario.getToken());
		});

		String expectedMessage = "Quiz Encerrado, abra um novo quiz";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void deveCriarUmaNovaPergunta() {
		
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
		assertFalse(quizResponseVO.getIdPergunta().equals(quizResponseVOComNovaPergunta.getIdPergunta()));
	}
	
	@Test
	void deveBuscarUmaPerguntaCriada() {
		
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
		
		QuizResponseVO novaPerguntaConsultada = quizService.getPergunta(quizResponseVOComNovaPergunta.getIdPergunta());
		
		assertNotNull(novaPerguntaConsultada.getIdPergunta());
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
		List<RankeadoVO> rankeados = quizService.getRankeados();
		
		assertNotNull(rankeados);
		assertTrue(rankeados.size() == 2);
	}
	
	@Test
	void deveEncerrarUmQuiz() {
		
		UsuarioVO usuario = criaUsuario();
		
		QuizResponseVO quizResponseVO = criaQuiz(usuario);
		
		Quiz quiz = quizService.encerrar(quizResponseVO.getIdQuiz(), usuario.getToken());
		
		assertEquals(quiz.isEncerrado(), true);
	}
	
	private UsuarioVO criaUsuario() {
		UsuarioDTO usuarioDTO = new UsuarioDTO();
		usuarioDTO.setUsername("Anderson Piotto");
		UsuarioVO usuarioCriado = usuarioService.cria(usuarioDTO);
		return usuarioCriado;
	}
	
	private QuizResponseVO criaQuiz() {
		UsuarioVO usuario = criaUsuario();
		
		String temaFilme = "Amor";
		String token = usuario.getToken();
		QuizResponseVO quizResponseVO = quizService.iniciar(temaFilme, token);
		return quizResponseVO;
	}
	
	private QuizResponseVO criaQuiz(UsuarioVO usuario) {
		String temaFilme = "Amor";
		String token = usuario.getToken();
		QuizResponseVO quizResponseVO = quizService.iniciar(temaFilme, token);
		return quizResponseVO;
	}

}
