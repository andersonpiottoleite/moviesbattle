package br.com.andersonpiotto.letscode.moviesbattle.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.andersonpiotto.letscode.moviesbattle.dto.UsuarioDTO;
import br.com.andersonpiotto.letscode.moviesbattle.exception.AutenticationException;
import br.com.andersonpiotto.letscode.moviesbattle.model.Usuario;
import br.com.andersonpiotto.letscode.moviesbattle.repository.UsuarioRepository;
import br.com.andersonpiotto.letscode.moviesbattle.vo.UsuarioVO;

/**
 * Classe de testes para <code>UsuarioServiceImpl</code>
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 21/03/2022
 */

@DataJpaTest
@ActiveProfiles("test")
class UsuarioServiceImplTest {

	private UsuarioServiceImpl usuarioService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@BeforeEach
	void antesDeCadaTeste() {
		usuarioService = new UsuarioServiceImpl(usuarioRepository);
	}

	@Test
	void deveCriarUmUsuario() {
		UsuarioVO usuarioCriado = criaUsuario();

		assertNotNull(usuarioCriado);
		assertNotNull(usuarioCriado.getToken());

	}

	@Test
	void deveAutenticarUmUsuarioCadastrado() {
		assertDoesNotThrow(() -> {
			UsuarioVO usuarioCriado = criaUsuario();
			usuarioService.autentica(usuarioCriado.getToken());
		});

	}

	@Test
	void naoDeveAutenticarUmUsuarioInexistente() {
		Exception exception = assertThrows(AutenticationException.class, () -> {
			usuarioService.autentica("token de usuario inexistente");
		});

		String expectedMessage = "Usuario não cadastrado";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));

	}
	
	@Test
	void deveBuscarUmUsuarioPeloToken() {
		UsuarioVO usuarioCriado = criaUsuario();
		Usuario usuarioBuscadoPorToken = usuarioService.buscaPorToken(usuarioCriado.getToken());
		
		assertNotNull(usuarioBuscadoPorToken);
		assertEquals(usuarioCriado.getToken(), usuarioBuscadoPorToken.getToken());
		assertEquals(usuarioCriado.getUsername(), usuarioBuscadoPorToken.getUsername());

	}

	private UsuarioVO criaUsuario() {
		UsuarioDTO usuarioDTO = new UsuarioDTO();
		usuarioDTO.setUsername("Anderson Piotto");
		UsuarioVO usuarioCriado = usuarioService.cria(usuarioDTO);
		return usuarioCriado;
	}

}