package mx.cinvestav.central.repository;

import mx.cinvestav.central.model.NodoFog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NodoFogRepository extends JpaRepository<NodoFog, Long> {
    List<NodoFog> findByEdificioId(Long edificioId);
}
