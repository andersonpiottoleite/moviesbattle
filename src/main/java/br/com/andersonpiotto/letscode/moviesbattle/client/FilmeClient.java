package br.com.andersonpiotto.letscode.moviesbattle.client;

import br.com.andersonpiotto.letscode.moviesbattle.dto.FilmeAvaliadoDTO;
import br.com.andersonpiotto.letscode.moviesbattle.dto.ListFilmesDTO;

/** Interface que define as operacoes de um FilmeClient
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 20/03/2022
 */

public interface FilmeClient {
	
	ListFilmesDTO getFilmes(String temaFilme);
	
	FilmeAvaliadoDTO getFilmesPorImdbID(String imdbID);
}
