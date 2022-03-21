package br.com.andersonpiotto.letscode.moviesbattle.service;

import br.com.andersonpiotto.letscode.moviesbattle.dto.UsuarioDTO;
import br.com.andersonpiotto.letscode.moviesbattle.model.Usuario;
import br.com.andersonpiotto.letscode.moviesbattle.vo.UsuarioVO;

/** Interface que define as operacoes de um UsuarioService
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 20/03/2022
 */

public interface UsuarioService {
	
	UsuarioVO cria(UsuarioDTO usuario);
	
	void autentica(String username);
	
	Usuario buscaPorToken(String token);
	
	String regerarToken(String tokenAntigo);

}
