package mx.cinvestav.central.controller;

import jakarta.validation.Valid;
import mx.cinvestav.central.config.SoloAdmin;
import mx.cinvestav.central.model.Edificio;
import mx.cinvestav.central.repository.EdificioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/edificios")
public class EdificioController {

    private final EdificioRepository repo;
    public EdificioController(EdificioRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Edificio> listar() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Edificio> obtener(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @SoloAdmin
    @PostMapping
    public ResponseEntity<Edificio> crear(@Valid @RequestBody Edificio e) {
        e.setId(null);
        return ResponseEntity.ok(repo.save(e));
    }

    @SoloAdmin
    @PutMapping("/{id}")
    public ResponseEntity<Edificio> actualizar(@PathVariable Long id, @Valid @RequestBody Edificio datos) {
        return repo.findById(id).map(e -> {
            e.setClave(datos.getClave());
            e.setNombre(datos.getNombre());
            e.setDescripcion(datos.getDescripcion());
            return ResponseEntity.ok(repo.save(e));
        }).orElse(ResponseEntity.notFound().build());
    }

    @SoloAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
