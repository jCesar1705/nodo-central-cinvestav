package mx.cinvestav.central.controller;

import mx.cinvestav.central.config.SoloAdmin;
import mx.cinvestav.central.service.SincronizacionService;
import mx.cinvestav.central.service.SincronizacionService.ResultadoSync;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sync")
public class SincronizacionController {

    private final SincronizacionService service;
    public SincronizacionController(SincronizacionService service) { this.service = service; }

    /** Empuja la config a TODOS los nodos FOG. */
    @SoloAdmin
    @PostMapping
    public List<ResultadoSync> sincronizarTodos() {
        return service.sincronizarTodos();
    }

    /** Empuja la config a un nodo FOG especifico. */
    @SoloAdmin
    @PostMapping("/{nodoId}")
    public ResultadoSync sincronizarUno(@PathVariable Long nodoId) {
        return service.sincronizarUno(nodoId);
    }
}
