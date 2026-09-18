package co.medily.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "devices")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String brand;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(length = 1000)
    private String image;

    @Column(length = 2000)
    private String description;

    private LocalDate releaseDate;

    @NotBlank
    @Column(nullable = false)
    private String category;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "device_features", joinColumns = @JoinColumn(name = "device_id"))
    @MapKeyColumn(name = "feature_name")
    @Column(name = "feature_value", length = 500)
    private Map<String, String> features = new LinkedHashMap<>();

    public Device() { }

    public Device(String name, String brand, BigDecimal price, String image, String description,
                  LocalDate releaseDate, String category, Map<String, String> features) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.image = image;
        this.description = description;
        this.releaseDate = releaseDate;
        this.category = category;
        this.features = features == null ? new LinkedHashMap<>() : new LinkedHashMap<>(features);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Map<String, String> getFeatures() { return features; }
    public void setFeatures(Map<String, String> features) { this.features = features == null ? new LinkedHashMap<>() : new LinkedHashMap<>(features); }
}
