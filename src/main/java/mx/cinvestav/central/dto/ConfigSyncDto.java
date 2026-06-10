package mx.cinvestav.central.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class ConfigSyncDto {

    public Instant generadoEn = Instant.now();
    public String  edificioClave;
    public String  edificioNombre;
    public List<ZonaDto>     zonas;
    public List<UsuarioDto>  usuarios;
    public Map<String,String> parametros;

    public static class ZonaDto {
        public Long    id;
        public String  nombre;
        public String  tipo;
        public Integer piso;
    }

    public static class UsuarioDto {
        public Long    id;
        public String  identificador;
        public String  nombre;
        public String  rol;
        public boolean activo;
        /** Contraseña del usuario (texto plano en Sprint 2; AES en Sprint-seguridad). */
        public String  passwordHash;
    }
}
