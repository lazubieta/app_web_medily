package co.medily.controller;

import co.medily.model.Device;
import co.medily.service.DeviceService;
import co.medily.service.AuthService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
    private final DeviceService service;
    private final AuthService authService;
    public DeviceController(DeviceService service, AuthService authService) {
        this.service = service;
        this.authService = authService;
    }

    @GetMapping
    public List<Device> findAll(@RequestParam(defaultValue = "") String search,
                          @RequestParam(defaultValue = "") String brand,
                          @RequestParam(defaultValue = "") String category) {
        return service.search(search, brand, category);
    }

    @GetMapping("/{id}")
    public Device findById(@PathVariable Long id) { return service.findById(id); }

    @GetMapping("/options")
    public Map<String, Object> options() { return Map.of("brands", service.brands(), "categories", service.categories()); }

    @PostMapping
    public ResponseEntity<Device> create(@CookieValue(value = AuthService.SESSION_COOKIE, required = false) String token,
                                         @Valid @RequestBody Device device) {
        requireAuth(token);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(device));
    }

    @PutMapping("/{id}")
    public Device update(@CookieValue(value = AuthService.SESSION_COOKIE, required = false) String token,
                         @PathVariable Long id, @Valid @RequestBody Device device) {
        requireAuth(token); return service.update(id, device);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@CookieValue(value = AuthService.SESSION_COOKIE, required = false) String token,
                                       @PathVariable Long id) {
        requireAuth(token); service.delete(id); return ResponseEntity.noContent().build();
    }

    private void requireAuth(String token) {
        if (!authService.isValid(token)) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "Se requiere una sesion de administrador");
    }

}
