package mx.cinvestav.central.controller;

import mx.cinvestav.central.config.SoloAdmin;
import mx.cinvestav.central.service.AlertaPropagacionService;
import mx.cinvestav.central.service.AlertaPropagacionService.ResultadoAlerta;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Propaga la activacion / desactivacion de emergencia a todos los nodos FOG.
 * Cuando el administrador pulsa "Activar emergencia" en el panel, este
 * controlador llama al /api/alertas/activar de CADA nodo FOG registrado.
 * Cada nodo FOG publica entonces la alerta via MQTT a sus dispositivos.
 */
@RestController
@RequestMapping("/api/emergencia")
public class AlertaCentralController {

    private final AlertaPropagacionService service;

    public AlertaCentralController(AlertaPropagacionService service) {
        this.service = service;
    }

    /**
     * POST /api/emergencia/activar
     * Body: { "descripcion": "Sismo intensidad 7", "nivelIntensidad": 7 }
     */
    @SoloAdmin
    @PostMapping("/activar")
    public List<ResultadoAlerta> activar(@RequestBody(required = false) Map<String, Object> body) {
        String descripcion     = body != null ? (String) body.getOrDefault("descripcion", "Alerta sismica") : "Alerta sismica";
        int    nivelIntensidad = body != null ? (int) body.getOrDefault("nivelIntensidad", 5) : 5;
        return service.activarEnTodos(descripcion, nivelIntensidad);
    }

    /**
     * POST /api/emergencia/desactivar
     */
    @SoloAdmin
    @PostMapping("/desactivar")
    public List<ResultadoAlerta> desactivar() {
        return service.desactivarEnTodos();
    }
}
