package mx.cinvestav.central.controller;

import jakarta.validation.Valid;
import mx.cinvestav.central.config.SoloAdmin;
import mx.cinvestav.central.dto.ZonaRequest;
import mx.cinvestav.central.model.Edificio;
import mx.cinvestav.central.model.Zona;
import mx.cinvestav.central.repository.EdificioRepository;
import mx.cinvestav.central.repository.ZonaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/zonas")
public class ZonaController {

    private final ZonaRepository repo;
    private final EdificioRepository edificioRepo;

    public ZonaController(ZonaRepository repo, EdificioRepository edificioRepo) {
        this.repo = repo;
        this.edificioRepo = edificioRepo;
    }

    /** Lista todas las zonas o, si se pasa ?edificioId=, solo las de ese edificio. */
    @GetMapping
    public List<Zona> listar(@RequestParam(required = false) Long edificioId) {
        return (edificioId == null) ? repo.findAll() : repo.findByEdificioId(edificioId);
    }

    @SoloAdmin
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ZonaRequest req) {
        Edificio edificio = edificioRepo.findById(req.edificioId).orElse(null);
        if (edificio == null) return ResponseEntity.badRequest().body("Edificio inexistente");
        Zona z = new Zona();
        aplicar(z, req, edificio);
        return ResponseEntity.ok(repo.save(z));
    }

    @SoloAdmin
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody ZonaRequest req) {
        Zona z = repo.findById(id).orElse(null);
        if (z == null) return ResponseEntity.notFound().build();
        Edificio edificio = edificioRepo.findById(req.edificioId).orElse(null);
        if (edificio == null) return ResponseEntity.badRequest().body("Edificio inexistente");
        aplicar(z, req, edificio);
        return ResponseEntity.ok(repo.save(z));
    }

    @SoloAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void aplicar(Zona z, ZonaRequest req, Edificio edificio) {
        z.setNombre(req.nombre);
        z.setTipo(req.tipo);
        z.setPiso(req.piso);
        z.setEdificio(edificio);
    }
}
