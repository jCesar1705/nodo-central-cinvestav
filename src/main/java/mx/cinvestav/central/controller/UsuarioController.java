package mx.cinvestav.central.controller;

import jakarta.validation.Valid;
import mx.cinvestav.central.config.SoloAdmin;
import mx.cinvestav.central.dto.UsuarioRequest;
import mx.cinvestav.central.model.Edificio;
import mx.cinvestav.central.model.Usuario;
import mx.cinvestav.central.repository.EdificioRepository;
import mx.cinvestav.central.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository  repo;
    private final EdificioRepository edificioRepo;

    public UsuarioController(UsuarioRepository repo, EdificioRepository edificioRepo) {
        this.repo         = repo;
        this.edificioRepo = edificioRepo;
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
        aplicar(u, req);
        return ResponseEntity.ok(repo.save(u));
    }

    @SoloAdmin
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest req) {
        Usuario u = repo.findById(id).orElse(null);
        if (u == null) return ResponseEntity.notFound().build();
        aplicar(u, req);
        return ResponseEntity.ok(repo.save(u));
    }

    @SoloAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void aplicar(Usuario u, UsuarioRequest req) {
        u.setIdentificador(req.identificador);
        u.setNombre(req.nombre);
        u.setRol(req.rol);
        u.setActivo(req.activo == null || req.activo);
        u.setEdificio(req.edificioId != null
                ? edificioRepo.findById(req.edificioId).orElse(null) : null);
        if (req.password != null && !req.password.isBlank()) {
            // TODO Sprint-seguridad: cifrar con AES-256 antes de guardar (R8).
            u.setPassword(req.password);
        }
    }
}
