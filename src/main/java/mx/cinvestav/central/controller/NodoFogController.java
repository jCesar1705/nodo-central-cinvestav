package mx.cinvestav.central.controller;

import jakarta.validation.Valid;
import mx.cinvestav.central.config.SoloAdmin;
import mx.cinvestav.central.dto.NodoFogRequest;
import mx.cinvestav.central.model.Edificio;
import mx.cinvestav.central.model.NodoFog;
import mx.cinvestav.central.repository.EdificioRepository;
import mx.cinvestav.central.repository.NodoFogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/nodos")
public class NodoFogController {

    private final NodoFogRepository repo;
    private final EdificioRepository edificioRepo;

    public NodoFogController(NodoFogRepository repo, EdificioRepository edificioRepo) {
        this.repo = repo;
        this.edificioRepo = edificioRepo;
    }

    @GetMapping
    public List<NodoFog> listar() { return repo.findAll(); }

    @SoloAdmin
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody NodoFogRequest req) {
        Edificio edificio = edificioRepo.findById(req.edificioId).orElse(null);
        if (edificio == null) return ResponseEntity.badRequest().body("Edificio inexistente");
        NodoFog n = new NodoFog();
        aplicar(n, req, edificio);
        return ResponseEntity.ok(repo.save(n));
    }

    @SoloAdmin
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody NodoFogRequest req) {
        NodoFog n = repo.findById(id).orElse(null);
        if (n == null) return ResponseEntity.notFound().build();
        Edificio edificio = edificioRepo.findById(req.edificioId).orElse(null);
        if (edificio == null) return ResponseEntity.badRequest().body("Edificio inexistente");
        aplicar(n, req, edificio);
        return ResponseEntity.ok(repo.save(n));
    }

    @SoloAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void aplicar(NodoFog n, NodoFogRequest req, Edificio edificio) {
        n.setNombre(req.nombre);
        n.setHost(req.host);
        n.setPuerto(req.puerto);
        n.setEdificio(edificio);
    }
}
