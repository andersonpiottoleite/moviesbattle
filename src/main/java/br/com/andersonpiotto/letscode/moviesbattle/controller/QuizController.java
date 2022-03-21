package br.com.andersonpiotto.letscode.moviesbattle.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.andersonpiotto.letscode.moviesbattle.dto.RespostaQuizRequestDTO;
import br.com.andersonpiotto.letscode.moviesbattle.service.QuizServiceImpl;
import br.com.andersonpiotto.letscode.moviesbattle.service.UsuarioServiceImpl;
import br.com.andersonpiotto.letscode.moviesbattle.vo.QuizResponseVO;
import br.com.andersonpiotto.letscode.moviesbattle.vo.RankeadoVO;
import io.swagger.annotations.ApiOperation;


/** Classe que representa um controller de quiz
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 19/03/2022
 */
@RestController
@RequestMapping("/quiz")
public class QuizController {
	
	@Autowired
	private QuizServiceImpl quizService;

	@Autowired
	private UsuarioServiceImpl usuarioServiceImpl;
	
	@ApiOperation(value = "Inicia um Quiz")
	@PostMapping("/iniciar/tema/{temaFilme}")
	public ResponseEntity<QuizResponseVO> iniciar(@PathVariable("temaFilme") String temaFilme, @RequestHeader("Token") String token) {
		usuarioServiceImpl.autentica(token);
		QuizResponseVO quizIniciado = quizService.iniciar(temaFilme, token);
		return ResponseEntity.ok().body(quizIniciado);
	}
	
	@ApiOperation(value = "Cria uma nova pergunta para o Quiz")
	@PostMapping("/nova-pergunta/{idQuiz}/tema/{temaFilme}")
	public ResponseEntity<QuizResponseVO> novaPergunta(@PathVariable("idQuiz") Long idQuiz, @PathVariable("temaFilme") String temaFilme, @RequestHeader("Token") String token) {
		usuarioServiceImpl.autentica(token);
		QuizResponseVO quizComNovaPergunta = quizService.novaPergunta(idQuiz, temaFilme, token);
		return ResponseEntity.ok().body(quizComNovaPergunta);
	}
	
	@ApiOperation(value = "Responde uma pergunta em um Quiz")
	@PostMapping("/responder")
	public ResponseEntity<?> responder(@RequestBody RespostaQuizRequestDTO respostaQuizRequest, @RequestHeader("Token") String token) {
		usuarioServiceImpl.autentica(token);
		quizService.responder(respostaQuizRequest, token);
		return ResponseEntity.ok().build();
	}
	
	@ApiOperation(value = "Encerra um Quiz")
	@PutMapping("/encerrar/{idQuiz}")
	public ResponseEntity<?> encerrar(@PathVariable("idQuiz") Long idQuiz, @RequestHeader("Token") String token) {
		usuarioServiceImpl.autentica(token);
		quizService.encerrar(idQuiz, token);
		return ResponseEntity.ok().build();
	}
	
	@ApiOperation(value = "Consulta uma pergunta já criada")
	@GetMapping("/pergunta/{idPergunta}")
	public ResponseEntity<QuizResponseVO> getPergunta(@PathVariable("idPergunta") Long idPergunta, @RequestHeader("Token") String token) {
		usuarioServiceImpl.autentica(token);
		QuizResponseVO quizIniciado = quizService.getPergunta(idPergunta);
		return ResponseEntity.ok().body(quizIniciado);
	}
	
	@ApiOperation(value = "Consulta o Ranking Geral dos participantes do Quiz")
	@GetMapping("/ranking")
	public ResponseEntity<List<RankeadoVO>> getRanking(@RequestHeader("Token") String token) {
		usuarioServiceImpl.autentica(token);
		List<RankeadoVO> rankeados = quizService.getRankeados();
		return ResponseEntity.ok().body(rankeados);
	}

}
