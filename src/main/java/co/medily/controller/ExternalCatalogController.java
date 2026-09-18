package co.medily.controller;

import co.medily.service.ExternalCatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/external")
public class ExternalCatalogController {
    private final ExternalCatalogService service;

    public ExternalCatalogController(ExternalCatalogService service) {
        this.service = service;
    }

    @GetMapping("/products")
    public Map<String, Object> products() {
        return service.products();
    }
}
