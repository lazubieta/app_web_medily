package co.medily.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatusCode;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleStatus(ResponseStatusException exception, HttpServletRequest request) {
        return error(exception.getStatusCode(), exception.getReason(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream().findFirst().map(error -> error.getField() + ": valor obligatorio o invalido").orElse("Datos invalidos");
        return error(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMalformedJson(HttpMessageNotReadableException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud no contiene un JSON valido", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(DataIntegrityViolationException exception, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "La operacion viola una restriccion de integridad de datos", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception exception, HttpServletRequest request) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrio un error interno en el servidor", request);
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatusCode status, String message, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", HttpStatus.valueOf(status.value()).getReasonPhrase());
        body.put("message", message == null ? "Error de solicitud" : message);
        body.put("path", request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
