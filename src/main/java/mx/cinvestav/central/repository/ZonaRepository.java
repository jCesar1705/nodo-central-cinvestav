package mx.cinvestav.central.repository;

import mx.cinvestav.central.model.Zona;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ZonaRepository extends JpaRepository<Zona, Long> {
    List<Zona> findByEdificioId(Long edificioId);
}
