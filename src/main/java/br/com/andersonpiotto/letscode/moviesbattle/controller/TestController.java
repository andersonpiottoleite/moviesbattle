package br.com.andersonpiotto.letscode.moviesbattle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/teste")
public class TestController {
	
	@GetMapping
	@ApiOperation(value = "TESTE")
	public ResponseEntity<?> teste(){
		return ResponseEntity.ok().build();
	}

}
