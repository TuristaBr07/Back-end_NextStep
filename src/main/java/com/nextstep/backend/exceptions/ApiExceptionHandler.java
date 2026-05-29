package com.nextstep.backend.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nextstep.backend.dtos.ApiErroDTO;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RegraNegocioException.class )
    public ResponseEntity<ApiErroDTO> tratarRegraNegocio(RegraNegocioException ex, HttpServletRequest request) {
        HttpStatus status = definirStatus(ex.getMessage());
        return ResponseEntity.status(status).body(criarErro(status, ex.getMessage(), request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErroDTO> tratarValidacao(MethodArgumentNotValidException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String mensagem = "Dados inválidos.";
        FieldError primeiroErro = ex.getBindingResult().getFieldError();
        if (primeiroErro != null) {
            String campo = primeiroErro.getField();
            String detalhe = primeiroErro.getDefaultMessage();
            mensagem = detalhe != null ? campo + ": " + detalhe : campo + " é inválido.";
        }

        return ResponseEntity.status(status).body(criarErro(status, mensagem, request));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErroDTO> tratarArgumentoInvalido(IllegalArgumentException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(criarErro(status, ex.getMessage(), request));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErroDTO> tratarErroGeral(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(criarErro(status, "Erro interno no servidor.", request));
    }

    private HttpStatus definirStatus(String mensagem) {
        if (mensagem == null) {
            return HttpStatus.BAD_REQUEST;
        }

        String mensagemNormalizada = mensagem.toLowerCase();

        if (mensagemNormalizada.contains("acesso negado")) {
            return HttpStatus.FORBIDDEN;
        }

        if (mensagemNormalizada.contains("não encontrada") || mensagemNormalizada.contains("não encontrado")) {
            return HttpStatus.NOT_FOUND;
        }

        return HttpStatus.BAD_REQUEST;
    }

    private ApiErroDTO criarErro(HttpStatus status, String mensagem, HttpServletRequest request) {
        return new ApiErroDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                request.getRequestURI()
        );
    }
}