package co.medily.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class DatabaseHealthController {
    private final DataSource dataSource;

    public DatabaseHealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/database")
    public ResponseEntity<Map<String, Object>> checkDatabase() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT 1");
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next() && resultSet.getInt(1) == 1) {
                return ResponseEntity.ok(Map.of(
                    "connected", true,
                    "message", "Conexion exitosa con la base de datos",
                    "database", connection.getCatalog()
                ));
            }

            return unavailable("La base de datos no respondio a la prueba SELECT 1");
        } catch (SQLException exception) {
            return unavailable("Conexion erronea con la base de datos: " + cleanMessage(exception));
        }
    }

    private ResponseEntity<Map<String, Object>> unavailable(String message) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
            "connected", false,
            "message", message
        ));
    }

    private String cleanMessage(SQLException exception) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? "verifique el servidor, puerto, usuario y contrasena" : message;
    }
}
