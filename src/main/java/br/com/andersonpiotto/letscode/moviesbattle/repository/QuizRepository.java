package br.com.andersonpiotto.letscode.moviesbattle.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.andersonpiotto.letscode.moviesbattle.model.Quiz;
import br.com.andersonpiotto.letscode.moviesbattle.model.Usuario;


/** Interface que configura um repository de <code>Quiz</code>
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 19/03/2022
 */
@Repository
public interface QuizRepository extends CrudRepository<Quiz, Long>{
	
	List<Quiz> findAllByUsuario(Usuario usuario);
	
	Optional<Quiz> findAllByIdAndUsuario_Token(Long id, String token);

}
