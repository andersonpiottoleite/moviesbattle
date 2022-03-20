package br.com.andersonpiotto.letscode.moviesbattle.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import br.com.andersonpiotto.letscode.moviesbattle.dto.FilmeAvaliadoDTO;
import br.com.andersonpiotto.letscode.moviesbattle.dto.ListFilmesDTO;
import br.com.andersonpiotto.letscode.moviesbattle.exception.IntegracaoException;

@Component
public class FilmeClient {
	
	@Autowired
	private RestTemplate restTemplate;
	
	public ListFilmesDTO getFilmes(String temaFilme){
		URI uri;
		try {
			
			int page = new Random().nextInt(50);
			
			uri = new URI("http://www.omdbapi.com/?s="+temaFilme+"&page="+page+"&apikey=c30d8101");
			ResponseEntity<ListFilmesDTO> result = restTemplate.getForEntity(uri, ListFilmesDTO.class);
			
			ListFilmesDTO filmes = result.getBody();
			
			if (! filmes.getResponse()) {
				throw new IntegracaoException("Ocorreu um erro na integração com o serviço de filmes");
			}
			
			return filmes;
			
		} catch (URISyntaxException e) {
			throw new IntegracaoException("Ocorreu um erro na integração com o serviço de filmes");
		}
	}
	
	public FilmeAvaliadoDTO getFilmesPorImdbID(String imdbID){
		URI uri;
		try {
			uri = new URI("https://www.omdbapi.com/?i=" + imdbID + "&plot=full&apikey=c30d8101");
			ResponseEntity<FilmeAvaliadoDTO> result = restTemplate.getForEntity(uri, FilmeAvaliadoDTO.class);
			return result.getBody();
			
		} catch (URISyntaxException e) {
			throw new IntegracaoException("Ocorreu um erro na integração com o serviço de filmes avaliados");
		}
	}
}
