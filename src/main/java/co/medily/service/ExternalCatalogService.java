package co.medily.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Adaptador de infraestructura para demostrar el consumo controlado de una API REST externa.
 * El endpoint no expone directamente el contrato del proveedor: lo envuelve con metadatos
 * propios y devuelve un resultado seguro cuando el tercero no esta disponible.
 */
@Service
public class ExternalCatalogService {
    private final RestClient client;
    private final String catalogUrl;

    public ExternalCatalogService(@Value("${medily.external.catalog-url}") String catalogUrl) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        this.client = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
        this.catalogUrl = catalogUrl;
    }

    public Map<String, Object> products() {
        try {
            JsonNode response = client.get().uri(catalogUrl).retrieve().body(JsonNode.class);
            JsonNode products = response == null ? null : response.path("products");
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("source", catalogUrl);
            result.put("fallback", false);
            result.put("data", products == null || products.isMissingNode() ? List.of() : products);
            return result;
        } catch (RestClientException exception) {
            return Map.of(
                    "source", catalogUrl,
                    "fallback", true,
                    "data", List.of(),
                    "message", "El servicio externo no esta disponible en este momento"
            );
        }
    }
}
