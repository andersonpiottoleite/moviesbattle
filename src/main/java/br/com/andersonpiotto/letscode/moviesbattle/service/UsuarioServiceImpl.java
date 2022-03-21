package br.com.andersonpiotto.letscode.moviesbattle.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.andersonpiotto.letscode.moviesbattle.config.JwtTokenUtil;
import br.com.andersonpiotto.letscode.moviesbattle.dto.UsuarioDTO;
import br.com.andersonpiotto.letscode.moviesbattle.exception.AutenticationException;
import br.com.andersonpiotto.letscode.moviesbattle.model.Usuario;
import br.com.andersonpiotto.letscode.moviesbattle.repository.UsuarioRepository;
import br.com.andersonpiotto.letscode.moviesbattle.vo.UsuarioVO;

/**
 * Classe que representa um service de <code>Usuario</code>
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 20/03/2022
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	public UsuarioServiceImpl() {}

	/** Construtor usado para testes
	 * 
	 * @param usuarioRepository
	 */
	public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
		super();
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public UsuarioVO cria(UsuarioDTO usuarioDTO) {
		String token = new JwtTokenUtil().generateToken(usuarioDTO);
		Usuario usuario = new Usuario(usuarioDTO.getUsername(), token);
		usuario = usuarioRepository.save(usuario);
		
		return new UsuarioVO(usuario.getUsername(), usuario.getToken());
	}

	//TODO pode ser melhorado usando o mecanismo de autenticacao do SpringSecurity
	@Override
	public void autentica(String token) {
		Usuario usuario = usuarioRepository.findByToken(token);
		if(Objects.isNull(usuario)) {
			throw new AutenticationException("Usuario não cadastrado");
		}
	}
	
	@Override
	public Usuario buscaPorToken(String token) {
		return usuarioRepository.findByToken(token);
	}

	@Override
	public String regerarToken(String tokenAntigo) {
		Usuario usuario = buscaPorToken(tokenAntigo);
		String novoToken = new JwtTokenUtil().generateToken(new UsuarioDTO(usuario.getUsername()));
		usuario.setToken(novoToken);
		usuario = usuarioRepository.save(usuario);
		
		return novoToken;
	}

}
