package br.com.andersonpiotto.letscode.moviesbattle.config;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import br.com.andersonpiotto.letscode.moviesbattle.dto.UsuarioDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/** Classe responsavel pela geração de token JWT
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 21/03/2022
 */

@Component
public class JwtTokenUtil implements Serializable {
	private static final long serialVersionUID = -2550185165626007488L;
	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

	private String secret = "usersecret";

	// Gera token para o usuario
	public String generateToken(UsuarioDTO usuario) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, usuario.getUsername());
	}

	// Cria o token
	private String doGenerateToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

}
