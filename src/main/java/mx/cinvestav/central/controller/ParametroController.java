package mx.cinvestav.central.controller;

import jakarta.validation.Valid;
import mx.cinvestav.central.config.SoloAdmin;
import mx.cinvestav.central.model.Parametro;
import mx.cinvestav.central.repository.ParametroRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/parametros")
public class ParametroController {

    private final ParametroRepository repo;
    public ParametroController(ParametroRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Parametro> listar() { return repo.findAll(); }

    @SoloAdmin
    @PostMapping
    public ResponseEntity<Parametro> crear(@Valid @RequestBody Parametro p) {
        p.setId(null);
        return ResponseEntity.ok(repo.save(p));
    }

    @SoloAdmin
    @PutMapping("/{id}")
    public ResponseEntity<Parametro> actualizar(@PathVariable Long id, @Valid @RequestBody Parametro datos) {
        return repo.findById(id).map(p -> {
            p.setClave(datos.getClave());
            p.setValor(datos.getValor());
            p.setDescripcion(datos.getDescripcion());
            return ResponseEntity.ok(repo.save(p));
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
