package mx.cinvestav.central.controller;

import jakarta.validation.Valid;
import mx.cinvestav.central.config.SoloAdmin;
import mx.cinvestav.central.dto.UsuarioRequest;
import mx.cinvestav.central.model.Edificio;
import mx.cinvestav.central.model.Usuario;
import mx.cinvestav.central.repository.EdificioRepository;
import mx.cinvestav.central.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository     repo;
    private final EdificioRepository    edificioRepo;
    private final BCryptPasswordEncoder encoder;

    public UsuarioController(UsuarioRepository repo, EdificioRepository edificioRepo,
                             BCryptPasswordEncoder encoder) {
        this.repo         = repo;
        this.edificioRepo = edificioRepo;
        this.encoder      = encoder;
    }

    @GetMapping
    public List<Usuario> listar(@RequestParam(required = false) Long edificioId) {
        return (edificioId == null) ? repo.findAll() : repo.findByEdificioId(edificioId);
    }

    @SoloAdmin
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody UsuarioRequest req) {
        if (repo.findByIdentificador(req.identificador).isPresent())
            return ResponseEntity.badRequest().body("Ya existe un usuario con ese identificador");
        Usuario u = new Usuario();
        aplicar(u, req, true);
        return ResponseEntity.ok(repo.save(u));
    }

    @SoloAdmin
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest req) {
        Usuario u = repo.findById(id).orElse(null);
        if (u == null) return ResponseEntity.notFound().build();
        aplicar(u, req, false);
        return ResponseEntity.ok(repo.save(u));
    }

    @SoloAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void aplicar(Usuario u, UsuarioRequest req, boolean esCreacion) {
        u.setIdentificador(req.identificador);
        u.setNombre(req.nombre);
        u.setRol(req.rol);
        u.setActivo(req.activo == null || req.activo);
        u.setEdificio(req.edificioId != null
                ? edificioRepo.findById(req.edificioId).orElse(null) : null);
        if (req.password != null && !req.password.isBlank()) {
            u.setPassword(encoder.encode(req.password));
        } else if (esCreacion) {
            u.setPassword(encoder.encode("1234"));
        }
        // En edición con password vacío se preserva el hash existente
    }
}
