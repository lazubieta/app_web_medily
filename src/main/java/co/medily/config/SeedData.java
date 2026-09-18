package co.medily.config;

import co.medily.model.AdminUser;
import co.medily.model.Category;
import co.medily.model.Device;
import co.medily.repository.AdminUserRepository;
import co.medily.repository.CategoryRepository;
import co.medily.repository.DeviceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class SeedData {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeedData.class);

    @Bean
    CommandLineRunner seed(DeviceRepository devices, CategoryRepository categories, AdminUserRepository users) {
        return args -> {
            try {
                if (categories.count() == 0) {
                    categories.saveAll(List.of(
                        new Category("Gama alta", "Dispositivos premium con especificaciones avanzadas"),
                        new Category("Gama media", "Equipos equilibrados para uso diario")
                    ));
                }
                if (devices.count() == 0) {
                    devices.saveAll(List.of(
                        device("iPhone 15 Pro", "Apple", "4899000", "2023-09-22", "Gama alta", "https://images.unsplash.com/photo-1695048133142-1a20484d2569?auto=format&fit=crop&w=900&q=80", "Smartphone premium con chasis de titanio, alto rendimiento y sistema de camaras avanzado.", Map.of("display", "6.1 pulgadas Super Retina XDR", "camera", "Triple camara de 48 MP", "storage", "128 GB", "battery", "Hasta 23 horas de video", "processor", "A17 Pro")),
                        device("Samsung Galaxy S24 Ultra", "Samsung", "5399000", "2024-01-31", "Gama alta", "https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?auto=format&fit=crop&w=900&q=80", "Dispositivo Android de alto desempeno con pantalla amplia, S Pen y camaras de largo alcance.", Map.of("display", "6.8 pulgadas Dynamic AMOLED 2X", "camera", "Camara principal de 200 MP", "storage", "256 GB", "battery", "5000 mAh", "processor", "Snapdragon 8 Gen 3")),
                        device("Xiaomi Redmi Note 13 Pro", "Xiaomi", "1499000", "2024-01-15", "Gama media", "https://images.unsplash.com/photo-1598327105666-5b89351aff97?auto=format&fit=crop&w=900&q=80", "Equipo equilibrado para uso diario, fotografia nitida y carga rapida.", Map.of("display", "6.67 pulgadas AMOLED", "camera", "Camara principal de 200 MP", "storage", "256 GB", "battery", "5100 mAh", "processor", "Snapdragon 7s Gen 2")),
                        device("Motorola Edge 50 Pro", "Motorola", "2599000", "2024-04-03", "Gama media", "https://images.unsplash.com/photo-1580910051074-3eb694886505?auto=format&fit=crop&w=900&q=80", "Telefono delgado con pantalla curva, carga veloz y experiencia Android limpia.", Map.of("display", "6.7 pulgadas pOLED", "camera", "Triple camara de 50 MP", "storage", "512 GB", "battery", "4500 mAh", "processor", "Snapdragon 7 Gen 3")),
                        device("Google Pixel 8", "Google", "3199000", "2023-10-12", "Gama alta", "https://images.unsplash.com/photo-1598965402089-897ce52e8355?auto=format&fit=crop&w=900&q=80", "Smartphone compacto enfocado en fotografia computacional y funciones inteligentes.", Map.of("display", "6.2 pulgadas OLED", "camera", "Doble camara de 50 MP", "storage", "128 GB", "battery", "4575 mAh", "processor", "Google Tensor G3")),
                        device("Honor Magic6 Lite", "Honor", "1299000", "2024-01-10", "Gama media", "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=80", "Movil de gran autonomia con pantalla AMOLED y diseno ligero para uso cotidiano.", Map.of("display", "6.78 pulgadas AMOLED", "camera", "Camara principal de 108 MP", "storage", "256 GB", "battery", "5300 mAh", "processor", "Snapdragon 6 Gen 1"))
                    ));
                }
                if (users.count() == 0) {
                    users.save(new AdminUser("admin", "admin@medily.com", new BCryptPasswordEncoder().encode("Medily2026*")));
                }
            } catch (Exception exception) {
                LOGGER.warn("No se pudo ejecutar la carga inicial. La aplicacion continuara para permitir verificar la conexion: {}", exception.getMessage());
            }
        };
    }

    private Device device(String name, String brand, String price, String releaseDate, String category, String image, String description, Map<String, String> features) {
        return new Device(name, brand, new BigDecimal(price), image, description, LocalDate.parse(releaseDate), category, new LinkedHashMap<>(features));
    }
}
