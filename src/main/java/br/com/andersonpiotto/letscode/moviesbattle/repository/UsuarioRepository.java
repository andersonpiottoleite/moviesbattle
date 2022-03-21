package br.com.andersonpiotto.letscode.moviesbattle.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.andersonpiotto.letscode.moviesbattle.model.Usuario;

/** Interface que configura um repository de <code>Usuario</code>
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 19/03/2022
 */
@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long>{

}
