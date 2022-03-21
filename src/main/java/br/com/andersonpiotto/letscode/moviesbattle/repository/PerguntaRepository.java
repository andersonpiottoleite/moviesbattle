package br.com.andersonpiotto.letscode.moviesbattle.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.andersonpiotto.letscode.moviesbattle.model.Pergunta;

/** Interface que configura um repository de <code>Pergunta</code>
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 19/03/2022
 */
@Repository
public interface PerguntaRepository extends CrudRepository<Pergunta, Long>{

}
