package mx.cinvestav.central.service;

import mx.cinvestav.central.model.EstadoNodo;
import mx.cinvestav.central.model.NodoFog;
import mx.cinvestav.central.repository.NodoFogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Propaga el comando de activar / desactivar emergencia a todos los nodos
 * FOG registrados. Cada nodo FOG, al recibir la activacion, publica la
 * alerta via MQTT a sus dispositivos conectados (CU-08).
 */
@Service
public class AlertaPropagacionService {

    private final NodoFogRepository nodoRepo;
    private final RestClient rest;

    public AlertaPropagacionService(NodoFogRepository nodoRepo,
                                    @Value("${central.sync.timeout-ms:2000}") int timeoutMs) {
        this.nodoRepo = nodoRepo;
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofMillis(timeoutMs).toMillis());
        factory.setReadTimeout((int) Duration.ofMillis(timeoutMs).toMillis());
        this.rest = RestClient.builder().requestFactory(factory).build();
    }

    /** Activa la emergencia en todos los nodos FOG y devuelve el resultado por nodo. */
    public List<ResultadoAlerta> activarEnTodos(String descripcion, int nivelIntensidad) {
        // Payload que espera el fog-node Sprint 1: ActivarEmergenciaRequest {tipo, severidad, mensaje}
        return nodoRepo.findAll().stream()
                .map(n -> propagar(n, "/api/emergencia/activar",
                        Map.of("tipo", "SISMICA",
                               "severidad", nivelIntensidad >= 7 ? "ALTA" : nivelIntensidad >= 4 ? "MEDIA" : "BAJA",
                               "mensaje", descripcion)))
                .collect(Collectors.toList());
    }

    /** Desactiva la emergencia en todos los nodos FOG. */
    public List<ResultadoAlerta> desactivarEnTodos() {
        return nodoRepo.findAll().stream()
                .map(n -> propagar(n, "/api/emergencia/desactivar", Map.of()))
                .collect(Collectors.toList());
    }

    private ResultadoAlerta propagar(NodoFog nodo, String endpoint, Object body) {
        try {
            rest.post()
                    .uri(nodo.getBaseUrl() + endpoint)
                    .header("X-Role", "ADMIN")   // El fog-node Sprint 1 usa ADMIN, no ADMINISTRADOR
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

            nodo.setEstado(EstadoNodo.ONLINE);
            nodo.setUltimaSincronizacion(Instant.now());
            nodoRepo.save(nodo);
            return new ResultadoAlerta(nodo.getId(), nodo.getNombre(), true, "OK");
        } catch (Exception ex) {
            nodo.setEstado(EstadoNodo.OFFLINE);
            nodoRepo.save(nodo);
            return new ResultadoAlerta(nodo.getId(), nodo.getNombre(), false,
                    "Sin conexión: " + ex.getClass().getSimpleName());
        }
    }

    public record ResultadoAlerta(Long nodoId, String nombre, boolean exito, String mensaje) {}
}
