package br.com.andersonpiotto.letscode.moviesbattle.service;

import java.util.List;

import br.com.andersonpiotto.letscode.moviesbattle.dto.RespostaQuizRequestDTO;
import br.com.andersonpiotto.letscode.moviesbattle.vo.QuizResponseVO;
import br.com.andersonpiotto.letscode.moviesbattle.vo.RankeadoVO;

/** Interface que define as operacoes de um QuizService
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 20/03/2022
 */

public interface QuizService {
	
	QuizResponseVO iniciar(String temaFilme, String token);
	
	void responder(RespostaQuizRequestDTO respostaQuizRequest, String token);
	
	QuizResponseVO novaPergunta(Long idQuiz, String temaFilme, String token);
	
	QuizResponseVO getPergunta(Long idPergunta);
	
	void encerrar(Long idQuiz, String token);

	List<RankeadoVO> getRankeados();


}
