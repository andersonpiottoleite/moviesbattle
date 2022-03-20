package br.com.andersonpiotto.letscode.moviesbattle.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.andersonpiotto.letscode.moviesbattle.model.Pergunta;

@Repository
public interface PerguntaRepository extends CrudRepository<Pergunta, Long>{

}
