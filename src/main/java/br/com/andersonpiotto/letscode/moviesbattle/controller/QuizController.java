package br.com.andersonpiotto.letscode.moviesbattle.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.andersonpiotto.letscode.moviesbattle.dto.RespostaQuizRequestDTO;
import br.com.andersonpiotto.letscode.moviesbattle.service.QuizService;
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
	private QuizService quizService;
	
	@ApiOperation(value = "Iniciar um Quiz")
	@GetMapping("/iniciar/tema/{temaFilme}")
	public ResponseEntity<QuizResponseVO> iniciar(@PathVariable("temaFilme") String temaFilme) {
		QuizResponseVO quizIniciado = quizService.iniciar(temaFilme);
		return ResponseEntity.ok().body(quizIniciado);
	}
	
	@ApiOperation(value = "Criar uma nova pergunta para o Quiz")
	@GetMapping("/nova-pergunta/{idQuiz}/tema/{temaFilme}")
	public ResponseEntity<QuizResponseVO> novaPergunta(@PathVariable("idQuiz") Long idQuiz, @PathVariable("temaFilme") String temaFilme) {
		QuizResponseVO quizComNovaPergunta = quizService.novaPergunta(idQuiz, temaFilme);
		return ResponseEntity.ok().body(quizComNovaPergunta);
	}
	
	@ApiOperation(value = "Responder uma pergunta em um Quiz")
	@PostMapping("/responder")
	public ResponseEntity<?> responder(@RequestBody RespostaQuizRequestDTO respostaQuizRequest) {
		quizService.responder(respostaQuizRequest);
		return ResponseEntity.ok().build();
	}
	
	@ApiOperation(value = "Encerrar um Quiz")
	@PostMapping("/encerrar/{idQuiz}")
	public ResponseEntity<?> encerrar(@PathVariable("idQuiz") Long idQuiz) {
		quizService.encerrar(idQuiz);
		return ResponseEntity.ok().build();
	}
	
	@ApiOperation(value = "Consultar uma pergunta já criada")
	@GetMapping("/pergunta/{idPergunta}")
	public ResponseEntity<QuizResponseVO> getPergunta(@PathVariable("idPergunta") Long idPergunta) {
		QuizResponseVO quizIniciado = quizService.getPergunta(idPergunta);
		return ResponseEntity.ok().body(quizIniciado);
	}
	
	@ApiOperation(value = "Consultar o Ranking Geral dos participantes do Quiz")
	@GetMapping("/ranking")
	public ResponseEntity<List<RankeadoVO>> getRanking() {
		List<RankeadoVO> rankeados = quizService.getRankeados();
		return ResponseEntity.ok().body(rankeados);
	}

}
