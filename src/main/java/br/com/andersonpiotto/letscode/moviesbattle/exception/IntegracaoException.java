package br.com.andersonpiotto.letscode.moviesbattle.exception;


/** Classe que representa uma exception de integração
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 19/03/2022
 */
public class IntegracaoException extends RuntimeException {
	
	public IntegracaoException() {
	}
	
	public IntegracaoException(String msg) {
		super(msg);
	}

}
