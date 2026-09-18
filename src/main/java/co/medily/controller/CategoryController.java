package co.medily.controller;

import co.medily.model.Category;
import co.medily.service.AuthService;
import co.medily.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class CategoryController {
    private final CategoryService service;
    private final AuthService authService;

    public CategoryController(CategoryService service, AuthService authService) { this.service = service; this.authService = authService; }

    @GetMapping
    public List<Category> findAll(@CookieValue(value = AuthService.SESSION_COOKIE, required = false) String token) { requireAuth(token); return service.findAll(); }

    @PostMapping
    public ResponseEntity<Category> create(@CookieValue(value = AuthService.SESSION_COOKIE, required = false) String token, @Valid @RequestBody Category category) {
        requireAuth(token); return ResponseEntity.status(HttpStatus.CREATED).body(service.create(category));
    }

    @PutMapping("/{id}")
    public Category update(@CookieValue(value = AuthService.SESSION_COOKIE, required = false) String token, @PathVariable Long id, @Valid @RequestBody Category category) {
        requireAuth(token); return service.update(id, category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@CookieValue(value = AuthService.SESSION_COOKIE, required = false) String token, @PathVariable Long id) {
        requireAuth(token); service.delete(id); return ResponseEntity.noContent().build();
    }

    private void requireAuth(String token) {
        if (!authService.isValid(token)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Se requiere una sesion de administrador");
    }
}
