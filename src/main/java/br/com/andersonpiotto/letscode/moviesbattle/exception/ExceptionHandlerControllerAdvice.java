package br.com.andersonpiotto.letscode.moviesbattle.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice extends ResponseEntityExceptionHandler{
  
  @ExceptionHandler(IntegracaoException.class)
  public ResponseEntity<ErrorMessage> resourceIntegracaoException(IntegracaoException ex, WebRequest request) {
    ErrorMessage message = new ErrorMessage(ex.getMessage());
    
    return new ResponseEntity<ErrorMessage>(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }
  
  @ExceptionHandler(IllegalStateException.class)
  public @ResponseBody ResponseEntity<ErrorMessage> resourceIllegalStateException(IllegalStateException ex, WebRequest request) {
    ErrorMessage message = new ErrorMessage(ex.getMessage());
    
    return new ResponseEntity<ErrorMessage>(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }
  
  @ExceptionHandler(IllegalArgumentException.class)
  public @ResponseBody ResponseEntity<ErrorMessage> resourceIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
    ErrorMessage message = new ErrorMessage(ex.getMessage());
    
    return new ResponseEntity<ErrorMessage>(message, HttpStatus.BAD_REQUEST);
  }
  
}
