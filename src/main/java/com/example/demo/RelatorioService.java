package com.example.demo;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class RelatorioService {
	
    public ResponseEntity<byte[]> gerarRelatorioId(int id, String username, String email, String senha) throws SQLException, JRException {
        // Conexão com o banco de dados
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "123456");

        // Carregar o arquivo .jrxml do classpath
        InputStream jrxmlStream = getClass().getClassLoader().getResourceAsStream("relatorios/relatorio-usuarios.jrxml");

        // Definir os parâmetros do JasperReports, incluindo a desativação do cache
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("REPORT_CONNECTION", conn);

        if (id != 0) {
            parametros.put("Id", id);
        }
        if (!username.isBlank()) {
            parametros.put("Username", username);
        }
        if (!email.isBlank()) {
            parametros.put("Email", email);
        }
        if (!senha.isBlank()) {
            parametros.put("Senha", senha);
        }


        // Compilar o arquivo .jrxml em um objeto JasperReport
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);

        // Executar uma consulta SQL e obter o ResultSet
        StringBuilder query = new StringBuilder("SELECT * FROM usuarios WHERE 1=1");

        if (id != 0) {
            query.append(" AND id = ?");
        }
        if (!username.isBlank()) {
            query.append(" AND usuario = ?");
        }
        if (!email.isBlank()) {
            query.append(" AND email = ?");
        }
        if (!senha.isBlank()) {
            query.append(" AND senha = ?");
        }

        PreparedStatement pstmt = conn.prepareStatement(query.toString());
        int paramIndex = 1;

        if (id != 0) {
            pstmt.setInt(paramIndex++, id);
        }
        if (!username.isBlank()) {
            pstmt.setString(paramIndex++, username);
        }
        if (!email.isBlank()) {
            pstmt.setString(paramIndex++, email);
        }
        if (!senha.isBlank()) {
            pstmt.setString(paramIndex++, senha);
        }

        ResultSet rs = pstmt.executeQuery();

        
        // Preencher o relatório com o ResultSet
        JRResultSetDataSource jrResultSetDataSource = new JRResultSetDataSource(rs);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, jrResultSetDataSource);

        // Exporta o relatório para um array de bytes (PDF)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
        byte[] relatorioPdf = baos.toByteArray();

        // Definir os headers para o download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "relatorio.pdf");
        headers.setContentLength(relatorioPdf.length);

        // Fechar a conexão com o banco de dados
        conn.close();

        // Retorna o PDF como resposta para o download
        return new ResponseEntity<>(relatorioPdf, headers, HttpStatus.OK);
    }
    
    public ResponseEntity<byte[]> gerarRelatorioGeral() throws SQLException, JRException {
        // Conexão com o banco de dados
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "123456");

        // Carregar o arquivo .jrxml do classpath
        InputStream jrxmlStream = getClass().getClassLoader().getResourceAsStream("relatorios/relatorio-usuarios-geral.jrxml");

        // Definir os parâmetros do JasperReports, incluindo a desativação do cache
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("REPORT_CONNECTION", conn);


        // Compilar o arquivo .jrxml em um objeto JasperReport
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);

        // Executar uma consulta SQL e obter o ResultSet
        String query = "select * FROM usuarios u";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        // Preencher o relatório com o ResultSet
        JRResultSetDataSource jrResultSetDataSource = new JRResultSetDataSource(rs);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, jrResultSetDataSource);

        // Exporta o relatório para um array de bytes (PDF)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
        byte[] relatorioPdf = baos.toByteArray();

        // Definir os headers para o download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "relatorio-geral.pdf");
        headers.setContentLength(relatorioPdf.length);

        // Fechar a conexão com o banco de dados
        conn.close();

        // Retorna o PDF como resposta para o download
        return new ResponseEntity<>(relatorioPdf, headers, HttpStatus.OK);
    }


}
