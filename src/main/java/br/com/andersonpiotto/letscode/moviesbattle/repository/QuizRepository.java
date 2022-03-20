package br.com.andersonpiotto.letscode.moviesbattle.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.andersonpiotto.letscode.moviesbattle.model.Quiz;
import br.com.andersonpiotto.letscode.moviesbattle.model.Usuario;

@Repository
public interface QuizRepository extends CrudRepository<Quiz, Long>{
	
	List<Quiz> findAllByUsuario(Usuario usuario);

}
