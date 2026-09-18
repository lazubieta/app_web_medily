package co.medily.service;

import co.medily.model.Category;
import co.medily.repository.CategoryRepository;
import co.medily.repository.DeviceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository repository;
    private final DeviceRepository deviceRepository;

    public CategoryService(CategoryRepository repository, DeviceRepository deviceRepository) {
        this.repository = repository; this.deviceRepository = deviceRepository;
    }

    public List<Category> findAll() { return repository.findAll(); }
    public Category findById(Long id) { return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria no encontrada")); }
    public Category create(Category category) { return repository.save(category); }
    public Category update(Long id, Category payload) {
        Category current = findById(id);
        current.setName(payload.getName()); current.setDescription(payload.getDescription());
        return repository.save(current);
    }
    public void delete(Long id) {
        Category category = findById(id);
        if (!deviceRepository.findByCategoryIgnoreCase(category.getName()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar una categoria con dispositivos asociados");
        }
        repository.delete(category);
    }
}
