package br.com.andersonpiotto.letscode.moviesbattle.exception;


/** Classe que representa uma exception de integração
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 19/03/2022
 */
@SuppressWarnings("serial")
public class AutenticationException extends RuntimeException {
	
	public AutenticationException() {
	}
	
	public AutenticationException(String msg) {
		super(msg);
	}

}
