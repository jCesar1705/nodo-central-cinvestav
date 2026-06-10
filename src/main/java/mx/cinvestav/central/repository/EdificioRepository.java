package mx.cinvestav.central.repository;

import mx.cinvestav.central.model.Edificio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EdificioRepository extends JpaRepository<Edificio, Long> {
}
