package br.com.andersonpiotto.letscode.moviesbattle.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import br.com.andersonpiotto.letscode.moviesbattle.dto.FilmeAvaliadoDTO;
import br.com.andersonpiotto.letscode.moviesbattle.dto.ListFilmesDTO;
import br.com.andersonpiotto.letscode.moviesbattle.exception.IntegracaoException;

/** Classe que representa um client de filmes
 * 
 * @author Anderson Piotto
 * @version 1.0.0
 * @since 19/03/2022
 */
@Component
public class FilmeClientImpl implements FilmeClient{
	
	private static Logger LOGGER = LoggerFactory.getLogger(FilmeClient.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	public ListFilmesDTO getFilmes(String temaFilme){
		URI uri;
		try {
			
			int page = new Random().nextInt(50);
			
			uri = new URI("http://www.omdbapi.com/?s="+temaFilme+"&page="+page+"&apikey=c30d8101");
			
			LOGGER.info("Realizando integração com o serviço da omdbapi na uri: " + uri);
			
			ResponseEntity<ListFilmesDTO> result = restTemplate.getForEntity(uri, ListFilmesDTO.class);
			
			ListFilmesDTO filmes = result.getBody();
			
			if (! filmes.getResponse()) {
				throw new IntegracaoException("Nenhum filme encontrado com o tema " + temaFilme);
			}
			
			LOGGER.info("Integração com o serviço da omdbapi na uri: " + uri + " realizado com sucesso!");
			
			return filmes;
			
		} catch (Exception e) {
			throw new IntegracaoException("Ocorreu um erro na integração com o serviço de filmes: " + e.getMessage());
		}
	}
	
	@Override
	public FilmeAvaliadoDTO getFilmesPorImdbID(String imdbID){
		URI uri;
		try {
			uri = new URI("https://www.omdbapi.com/?i=" + imdbID + "&plot=full&apikey=c30d8101");
			
			LOGGER.info("Realizando integração com o serviço da omdbapi na uri: " + uri);
			
			ResponseEntity<FilmeAvaliadoDTO> result = restTemplate.getForEntity(uri, FilmeAvaliadoDTO.class);
			
			LOGGER.info("Integração com o serviço da omdbapi na uri: " + uri + " realizado com sucesso!");
			
			return result.getBody();
			
		} catch (URISyntaxException e) {
			throw new IntegracaoException("Ocorreu um erro na integração com o serviço de filmes avaliados");
		}
	}
}
