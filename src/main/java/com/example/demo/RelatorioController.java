package com.example.demo;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.sf.jasperreports.engine.JRException;

@RestController
@RequestMapping("/relatorio")
public class RelatorioController {
	@Autowired
	private RelatorioService service;

	
	@GetMapping("/gerar/id")
	public ResponseEntity gerarRelatorio(@RequestParam(required = false, defaultValue = "0") int id,
										@RequestParam(required = false) String username, 
										@RequestParam(required = false) String email,
										@RequestParam(required = false) String senha) throws SQLException, JRException, IOException {
		
		return service.gerarRelatorioId(id,username,email,senha);
	}
	
	@GetMapping("/gerar")
	public ResponseEntity gerarRelatorioGeral() throws SQLException, JRException, IOException {
		
		return service.gerarRelatorioGeral();
	}
}
