package co.medily.service;

import co.medily.model.AdminUser;
import co.medily.repository.AdminUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    public static final String SESSION_COOKIE = "medily_session";
    private final AdminUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Map<String, Instant> tokens = new ConcurrentHashMap<>();

    public AuthService(AdminUserRepository userRepository) { this.userRepository = userRepository; }

    public String login(String identity, String password) {
        if (identity == null || password == null) return null;
        AdminUser user = userRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(identity, identity).orElse(null);
        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) return null;
        String token = UUID.randomUUID().toString();
        tokens.put(token, Instant.now().plusSeconds(60 * 60 * 8));
        return token;
    }

    public boolean isValid(String token) {
        if (token == null || token.isBlank()) return false;
        Instant expiration = tokens.get(token);
        if (expiration == null) return false;
        if (expiration.isBefore(Instant.now())) { tokens.remove(token); return false; }
        return true;
    }

    public void logout(String token) { if (token != null) tokens.remove(token); }

    public BCryptPasswordEncoder encoder() { return passwordEncoder; }
}
