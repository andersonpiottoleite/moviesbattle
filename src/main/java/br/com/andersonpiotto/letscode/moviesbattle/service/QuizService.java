package br.com.andersonpiotto.letscode.moviesbattle.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.andersonpiotto.letscode.moviesbattle.client.FilmeClient;
import br.com.andersonpiotto.letscode.moviesbattle.dto.FilmeAvaliadoDTO;
import br.com.andersonpiotto.letscode.moviesbattle.dto.FilmeDTO;
import br.com.andersonpiotto.letscode.moviesbattle.dto.ListFilmesDTO;
import br.com.andersonpiotto.letscode.moviesbattle.dto.RespostaQuizRequestDTO;
import br.com.andersonpiotto.letscode.moviesbattle.model.Pergunta;
import br.com.andersonpiotto.letscode.moviesbattle.model.Quiz;
import br.com.andersonpiotto.letscode.moviesbattle.model.Resposta;
import br.com.andersonpiotto.letscode.moviesbattle.model.Usuario;
import br.com.andersonpiotto.letscode.moviesbattle.repository.PerguntaRepository;
import br.com.andersonpiotto.letscode.moviesbattle.repository.QuizRepository;
import br.com.andersonpiotto.letscode.moviesbattle.repository.UsuarioRepository;
import br.com.andersonpiotto.letscode.moviesbattle.vo.QuizResponseVO;
import br.com.andersonpiotto.letscode.moviesbattle.vo.RankeadoVO;
import br.com.andersonpiotto.letscode.moviesbattle.vo.ResumoFilmeVO;

@Service
public class QuizService {

	@Autowired
	private FilmeClient filmeClient;
	
	@Autowired
	private QuizRepository quizRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private PerguntaRepository perguntaRepository;

	public QuizResponseVO iniciar(String temaFilme) {
		
		List<FilmeDTO> filmes = getFilmesAleatorios(temaFilme);
		FilmeDTO primeiroFilme = filmes.get(0);
		FilmeDTO segundoFilme = filmes.get(1);
		
		Quiz quiz = criaQuiz(primeiroFilme, segundoFilme);

		return new QuizResponseVO(criaResumoFilmeVO(primeiroFilme), criaResumoFilmeVO(segundoFilme), quiz.getId(), quiz.getPerguntas().get(0).getId());
	}
	
	private Quiz criaQuiz(FilmeDTO primeiroFilme, FilmeDTO segundoFilme) {
		// TODO pegar ususario da sessao
		Usuario usuario = new Usuario("login", "senha", "Anderson Piotto");
		
		Quiz quiz = new Quiz(usuario);
		
		adicionaPergunta(primeiroFilme, segundoFilme, quiz);
		
		return quiz;
	}

	private Quiz adicionaPergunta(FilmeDTO primeiroFilme, FilmeDTO segundoFilme, Quiz quiz) {
		Pergunta pergunta = new Pergunta(primeiroFilme.getImdbID(), segundoFilme.getImdbID());
		quiz.addPergunta(pergunta);
		pergunta.setQuiz(quiz);
		
		return quizRepository.save(quiz);
	}

	private ResumoFilmeVO criaResumoFilmeVO(FilmeDTO primeiroFilme) {
		return new ResumoFilmeVO(primeiroFilme.getTitulo(), primeiroFilme.getAno(), primeiroFilme.getImdbID(), primeiroFilme.getPoster());
	}

	@Transactional
	public void responder(RespostaQuizRequestDTO respostaQuizRequest) {
		Quiz quiz = quizRepository.findById(respostaQuizRequest.getIdQuiz()).orElseThrow(() -> new IllegalArgumentException("Quiz não encontrado"));
		
		Pergunta pergunta = perguntaRepository.findById(respostaQuizRequest.getIdPergunta()).orElseThrow(() -> new IllegalArgumentException("Pergunta não encontrada"));
		
		validaCondicoesResposta(respostaQuizRequest, quiz, pergunta);
		
		FilmeAvaliadoDTO filmeAvaliado1 = filmeClient.getFilmesPorImdbID(pergunta.getPrimeiraOpcaoImdbID());
		FilmeAvaliadoDTO filmeAvaliado2 = filmeClient.getFilmesPorImdbID(pergunta.getSegundaOpcaoImdbID());
		
		
		FilmeAvaliadoDTO melhorFilmeAvaliado = getMelhorAvaliado(filmeAvaliado1, filmeAvaliado2);
		
		Resposta resposta = new Resposta();
		resposta.setImdbIDRespondido(respostaQuizRequest.getImdbIDRespondido());
		
		if (resposta.getImdbIDRespondido().equals(melhorFilmeAvaliado.getImdbID())) {
			resposta.setCorreta(true);
			quiz.setQuantidadeRespostasCorretas(quiz.getQuantidadeRespostasCorretas() + 1);
			
		} else {
			resposta.setCorreta(false);
			quiz.setQuantidadeErros(quiz.getQuantidadeErros() + 1);
			if (quiz.getQuantidadeErros() == 3) {
				quiz.encerra();
			}
		}
		
		pergunta.setResposta(resposta);
		pergunta.respondida();
		
		quizRepository.save(quiz);
		
	}

	private void validaCondicoesResposta(RespostaQuizRequestDTO respostaQuizRequest, Quiz quiz, Pergunta pergunta) {
		if (quiz.isEncerrado()) {
			throw new IllegalArgumentException("Quiz Encerrado, abra um novo quiz!");
		}
		
		if (pergunta.isRespondida()) {
			throw new IllegalArgumentException("Pergunta já respondida, crie uma nova pergunta!");
		}
		
		if (!respostaQuizRequest.getImdbIDRespondido().equals(pergunta.getPrimeiraOpcaoImdbID()) &&
				!respostaQuizRequest.getImdbIDRespondido().equals(pergunta.getSegundaOpcaoImdbID())) {
			
			throw new IllegalArgumentException("Opção não encontrada para a pergunta, informe um Filme que tenha um dos ImdbID's: "
				+ pergunta.getPrimeiraOpcaoImdbID() + " ou " +  pergunta.getSegundaOpcaoImdbID());
		}
	}

	private void existePerguntaNaoRespondida(Quiz quiz) {
		Optional<Pergunta> optional = quiz.getPerguntas().stream().filter( p -> Objects.isNull(p.getResposta())).findAny();
		if (! optional.isEmpty()) {
			throw new IllegalStateException("Existe uma Pergunta pendente de resposta! Responda a Pergunta: " + optional.get().getId());
		}
	}
	
	private FilmeAvaliadoDTO getMelhorAvaliado(FilmeAvaliadoDTO filmeAvaliado1, FilmeAvaliadoDTO filmeAvaliado2) {
		return filmeAvaliado1.getImdbRating() > filmeAvaliado2.getImdbRating() ? filmeAvaliado1 : filmeAvaliado2;
	}
	
	public void encerrar(Long idQuiz) {
		Quiz quiz = quizRepository.findById(idQuiz).orElseThrow(() -> new IllegalArgumentException("Quiz não encontrado"));
		quiz.encerra();
		quizRepository.save(quiz);
	}

	public QuizResponseVO novaPergunta(Long idQuiz, String temaFilme) {
		Quiz quiz = quizRepository.findById(idQuiz).orElseThrow(() -> new IllegalArgumentException("Quiz não encontrado"));
		
		existePerguntaNaoRespondida(quiz);
				
		List<FilmeDTO> filmes = null;
		FilmeDTO primeiroFilme = null;
		FilmeDTO segundoFilme = null;
		
		do {
			filmes = getFilmesAleatorios(temaFilme);
			primeiroFilme = filmes.get(0);
			segundoFilme = filmes.get(1);
			
		} while(combinacaoFilmesJaUsada(quiz, primeiroFilme, segundoFilme));
		
		quiz = adicionaPergunta(primeiroFilme, segundoFilme, quiz);
		
		Pergunta pergunta = quiz.getPerguntas().stream().max(Comparator.comparing(Pergunta::getId))
	      .orElseThrow(NoSuchElementException::new);
		
		return new QuizResponseVO(criaResumoFilmeVO(primeiroFilme), criaResumoFilmeVO(segundoFilme), quiz.getId(), pergunta.getId());
	}
	
	private boolean combinacaoFilmesJaUsada(Quiz quiz, FilmeDTO primeiroFilme, FilmeDTO segundoFilme) {
		return quiz.getPerguntas().stream().filter(p -> 
				(p.getPrimeiraOpcaoImdbID().equals(primeiroFilme.getImdbID()) && p.getSegundaOpcaoImdbID().equals(segundoFilme.getImdbID())) || 
				(p.getPrimeiraOpcaoImdbID().equals(segundoFilme.getImdbID()) && p.getSegundaOpcaoImdbID().equals(primeiroFilme.getImdbID()))
		).findAny().isPresent();
	}
	
	private List<FilmeDTO> getFilmesAleatorios(String temaFilme) {
		ListFilmesDTO filmes = filmeClient.getFilmes(temaFilme);
		
		final int quantidadeMaximaFilmes = filmes.getFilmes().size() -1;
		int primeiroNumeroAleatorio = geraNumeroAleatorio(quantidadeMaximaFilmes);
		int segundoNumeroAleatorio = 0;
		
		do {			
			segundoNumeroAleatorio = geraNumeroAleatorio(quantidadeMaximaFilmes);
		} while (primeiroNumeroAleatorio == segundoNumeroAleatorio);
		
		FilmeDTO primeiroFilme = getFilme(filmes, primeiroNumeroAleatorio);
		FilmeDTO segundoFilme = getFilme(filmes, segundoNumeroAleatorio);
		
		return Arrays.asList(primeiroFilme, segundoFilme);
	}
	
	private int geraNumeroAleatorio(int limite) {
		return new Random().nextInt(limite);
	}
	
	private FilmeDTO getFilme(ListFilmesDTO filmes, int numeroAleatorio) {
		return filmes.getFilmes().get(numeroAleatorio);
	}

	public QuizResponseVO getPergunta(Long idPergunta) {
		Pergunta pergunta = perguntaRepository.findById(idPergunta).orElseThrow(() -> new IllegalArgumentException("Pergunta não encontrada"));
		
		FilmeAvaliadoDTO filmeAvaliado1 = filmeClient.getFilmesPorImdbID(pergunta.getPrimeiraOpcaoImdbID());
		FilmeAvaliadoDTO filmeAvaliado2 = filmeClient.getFilmesPorImdbID(pergunta.getSegundaOpcaoImdbID());
		
		return new QuizResponseVO(criaResumoFilmeVO(filmeAvaliado1), criaResumoFilmeVO(filmeAvaliado2), pergunta.getQuiz().getId(), pergunta.getId());
	}
	
	private ResumoFilmeVO criaResumoFilmeVO(FilmeAvaliadoDTO filmeAvaliado) {
		return new ResumoFilmeVO(filmeAvaliado.getTitulo(), filmeAvaliado.getAno(), filmeAvaliado.getImdbID(), filmeAvaliado.getPoster());
	}

	public List<RankeadoVO> getRankeados() {
		List<Quiz> quizes = null;		
		List<RankeadoVO> rankeados = new ArrayList<RankeadoVO>();
		
		Iterator<Usuario> iterator = usuarioRepository.findAll().iterator();
		
		while (iterator.hasNext()) {
			 Usuario usuario = iterator.next();
			
			 quizes = quizRepository.findAllByUsuario(usuario);
			 
			 RankeadoVO rankeado = new RankeadoVO();
			 
           	 rankeado.setNome(usuario.getNome());
           	 
			 int quantidadeQuizes = quizes.size();
			 rankeado.setQuantidadeDeQuizesRespondidos(quantidadeQuizes);
			 
			 int totalQuantidadeRespostasCorretas = quizes.stream()
			 .mapToInt(q -> q.getQuantidadeRespostasCorretas())
			 .sum();
			 
			 rankeado.setPorcentagemAcerto(totalQuantidadeRespostasCorretas / quantidadeQuizes);
			 
			 rankeados.add(rankeado);
		}
		
		Collections.sort(rankeados,new Comparator<RankeadoVO>(){
			
            public int compare(RankeadoVO r1,RankeadoVO r2){
            	if(r1.getPorcentagemAcerto() > r2.getPorcentagemAcerto()) {
            		return 1;
            	}else if(r1.getPorcentagemAcerto() > r2.getPorcentagemAcerto()) {
            		return -1;
            	}
            	
            	return 0;
        }});
		
		int posicaoRanking = 0;
		
		for (RankeadoVO rankeadoVO : rankeados) {			
			rankeadoVO.setPosicaoRanking(posicaoRanking ++);
		}
		
		return rankeados;
	}

}
