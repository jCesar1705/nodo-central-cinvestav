package mx.cinvestav.central.repository;

import mx.cinvestav.central.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    List<Usuario> findByEdificioId(Long edificioId);
    Optional<Usuario> findByIdentificador(String identificador);
}
