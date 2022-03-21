package br.com.andersonpiotto.letscode.moviesbattle.exception;


/** Classe para exibição de mensagem de erros
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 19/03/2022
 */
public class ErrorMessage {

	private String message;

	public ErrorMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
