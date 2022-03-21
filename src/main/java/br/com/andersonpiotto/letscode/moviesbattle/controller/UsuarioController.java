package br.com.andersonpiotto.letscode.moviesbattle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.andersonpiotto.letscode.moviesbattle.dto.UsuarioDTO;
import br.com.andersonpiotto.letscode.moviesbattle.service.UsuarioServiceImpl;
import br.com.andersonpiotto.letscode.moviesbattle.vo.UsuarioVO;
import io.swagger.annotations.ApiOperation;


/** Classe que representa um controller de usuario
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 19/03/2022
 */
@RestController
@RequestMapping("/usuario")
public class UsuarioController {
	
	@Autowired
	private UsuarioServiceImpl usuarioService;
	
	@ApiOperation(value = "Cria um usuario")
	@PostMapping
	public ResponseEntity<UsuarioVO> criar(@RequestBody UsuarioDTO usuario) {
		UsuarioVO usuarioVO = usuarioService.cria(usuario);
		return ResponseEntity.ok(usuarioVO);
	}
	
	@ApiOperation(value = "Gera um novo token para o usuario")
	@PutMapping("/regerar-token")
	public ResponseEntity<String> regerarToken(@RequestHeader("Token") String tokenAntigo) {
		usuarioService.autentica(tokenAntigo);
		String novoToken = usuarioService.regerarToken(tokenAntigo);
		return ResponseEntity.ok(novoToken);
	}
}
