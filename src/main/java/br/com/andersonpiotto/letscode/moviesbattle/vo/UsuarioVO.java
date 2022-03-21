package br.com.andersonpiotto.letscode.moviesbattle.vo;

/** Classe que representa um View Object para Usuario
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 19/03/2022
 */
public class UsuarioVO {
	
	private String username;
	private String token;

	public UsuarioVO(String username, String token) {
		this.username = username;
		this.token = token;
	}

	public UsuarioVO() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
