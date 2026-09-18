package co.medily.service;

import co.medily.model.Device;
import co.medily.repository.DeviceRepository;
import co.medily.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DeviceService {
    private final DeviceRepository repository;
    private final CategoryRepository categoryRepository;

    public DeviceService(DeviceRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    public List<Device> search(String search, String brand, String category) {
        return repository.search(normalize(search), normalize(brand), normalize(category));
    }

    public Device findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispositivo no encontrado"));
    }

    public Device create(Device device) {
        validateCategory(device.getCategory());
        return repository.save(device);
    }

    public Device update(Long id, Device payload) {
        Device current = findById(id);
        current.setName(payload.getName());
        current.setBrand(payload.getBrand());
        current.setPrice(payload.getPrice());
        current.setImage(payload.getImage());
        current.setDescription(payload.getDescription());
        current.setReleaseDate(payload.getReleaseDate());
        current.setCategory(payload.getCategory());
        current.setFeatures(payload.getFeatures());
        validateCategory(current.getCategory());
        return repository.save(current);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispositivo no encontrado");
        repository.deleteById(id);
    }

    public List<String> brands() {
        return repository.findAll().stream().map(Device::getBrand).filter(java.util.Objects::nonNull).distinct().sorted().toList();
    }

    public List<String> categories() {
        return repository.findAll().stream().map(Device::getCategory).filter(java.util.Objects::nonNull).distinct().sorted().toList();
    }

    private String normalize(String value) { return value == null ? "" : value.trim(); }

    private void validateCategory(String category) {
        if (category == null || category.isBlank() || !categoryRepository.existsByNameIgnoreCase(category.trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoria indicada no existe");
        }
    }
}
