package co.medily.controller;

import co.medily.service.AuthService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.Map;
import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.identity(), request.password());
        if (token == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Usuario o contrasena incorrectos"));
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, sessionCookie(token, Duration.ofHours(8)).toString())
            .body(Map.of("message", "Autenticacion exitosa"));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(value = AuthService.SESSION_COOKIE, required = false) String token) {
        authService.logout(token);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, sessionCookie("", Duration.ZERO).toString())
            .body(Map.of("message", "Sesion cerrada"));
    }

    @GetMapping("/session")
    public ResponseEntity<?> session(@CookieValue(value = AuthService.SESSION_COOKIE, required = false) String token) {
        return ResponseEntity.ok(Map.of("authenticated", authService.isValid(token)));
    }

    private ResponseCookie sessionCookie(String value, Duration maxAge) {
        return ResponseCookie.from(AuthService.SESSION_COOKIE, value)
            .httpOnly(true)
            .secure(false)
            .sameSite("Lax")
            .path("/")
            .maxAge(maxAge)
            .build();
    }

    public record LoginRequest(@NotBlank String identity, @NotBlank String password) { }
}
