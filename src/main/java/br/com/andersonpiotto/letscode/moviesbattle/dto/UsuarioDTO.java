package br.com.andersonpiotto.letscode.moviesbattle.dto;

public class UsuarioDTO {

	private String username;

	public UsuarioDTO(String username) {
		this.username = username;
	}

	public UsuarioDTO() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
