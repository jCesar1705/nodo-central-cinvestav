package mx.cinvestav.central.service;

import mx.cinvestav.central.dto.ConfigSyncDto;
import mx.cinvestav.central.model.*;
import mx.cinvestav.central.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SincronizacionService {

    private final NodoFogRepository    nodoRepo;
    private final ZonaRepository       zonaRepo;
    private final UsuarioRepository    usuarioRepo;
    private final ParametroRepository  parametroRepo;
    private final RestClient           rest;

    public SincronizacionService(NodoFogRepository nodoRepo,
                                 ZonaRepository zonaRepo,
                                 UsuarioRepository usuarioRepo,
                                 ParametroRepository parametroRepo,
                                 @Value("${central.sync.timeout-ms:2000}") int timeoutMs) {
        this.nodoRepo     = nodoRepo;
        this.zonaRepo     = zonaRepo;
        this.usuarioRepo  = usuarioRepo;
        this.parametroRepo = parametroRepo;
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofMillis(timeoutMs).toMillis());
        factory.setReadTimeout((int) Duration.ofMillis(timeoutMs).toMillis());
        this.rest = RestClient.builder().requestFactory(factory).build();
    }

    public List<ResultadoSync> sincronizarTodos() {
        return nodoRepo.findAll().stream()
                .map(this::sincronizar).collect(Collectors.toList());
    }

    public ResultadoSync sincronizarUno(Long nodoId) {
        NodoFog nodo = nodoRepo.findById(nodoId).orElse(null);
        if (nodo == null) return new ResultadoSync(nodoId, null, false, "Nodo inexistente");
        return sincronizar(nodo);
    }

    private ResultadoSync sincronizar(NodoFog nodo) {
        ConfigSyncDto config = construirConfig(nodo);
        try {
            rest.post()
                .uri(nodo.getBaseUrl() + "/config/sync")
                .header("X-Role", "ADMINISTRADOR")
                .body(config)
                .retrieve()
                .toBodilessEntity();
            nodo.setEstado(EstadoNodo.ONLINE);
            nodo.setUltimaSincronizacion(Instant.now());
            nodoRepo.save(nodo);
            return new ResultadoSync(nodo.getId(), nodo.getNombre(), true, "Sincronizado");
        } catch (Exception ex) {
            nodo.setEstado(EstadoNodo.OFFLINE);
            nodoRepo.save(nodo);
            return new ResultadoSync(nodo.getId(), nodo.getNombre(), false,
                    "Sin conexion: " + ex.getClass().getSimpleName());
        }
    }

    private ConfigSyncDto construirConfig(NodoFog nodo) {
        Edificio edificio = nodo.getEdificio();
        ConfigSyncDto dto = new ConfigSyncDto();
        dto.edificioClave  = edificio.getClave();
        dto.edificioNombre = edificio.getNombre();

        dto.zonas = zonaRepo.findByEdificioId(edificio.getId()).stream().map(z -> {
            var zd = new ConfigSyncDto.ZonaDto();
            zd.id = z.getId(); zd.nombre = z.getNombre();
            zd.tipo = z.getTipo().name(); zd.piso = z.getPiso();
            return zd;
        }).collect(Collectors.toList());

        dto.usuarios = usuarioRepo.findByEdificioId(edificio.getId()).stream().map(u -> {
            var ud = new ConfigSyncDto.UsuarioDto();
            ud.id            = u.getId();
            ud.identificador = u.getIdentificador();
            ud.nombre        = u.getNombre();
            ud.rol           = u.getRol().name();
            ud.activo        = u.isActivo();
            ud.passwordHash  = u.getPassword();
            return ud;
        }).collect(Collectors.toList());

        dto.parametros = new LinkedHashMap<>();
        for (Parametro p : parametroRepo.findAll()) {
            dto.parametros.put(p.getClave(), p.getValor());
        }
        return dto;
    }

    public record ResultadoSync(Long nodoId, String nombre, boolean exito, String mensaje) {}
}
