package co.medily.repository;

import co.medily.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    @Query("select d from Device d where "
            + "(:search = '' or lower(d.name) like lower(concat('%', :search, '%')) "
            + "or lower(d.brand) like lower(concat('%', :search, '%')) "
            + "or lower(d.category) like lower(concat('%', :search, '%')) "
            + "or lower(d.description) like lower(concat('%', :search, '%'))) "
            + "and (:brand = '' or lower(d.brand) = lower(:brand)) "
            + "and (:category = '' or lower(d.category) = lower(:category))")
    List<Device> search(@Param("search") String search, @Param("brand") String brand, @Param("category") String category);

    List<Device> findByCategoryIgnoreCase(String category);
}
