package mx.cinvestav.central.config;

import mx.cinvestav.central.model.*;
import mx.cinvestav.central.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(EdificioRepository edificios, ZonaRepository zonas,
                           NodoFogRepository nodos, UsuarioRepository usuarios,
                           ParametroRepository parametros) {
        return args -> {
            if (edificios.count() > 0) return;

            Edificio a = edificios.save(new Edificio("A", "Edificio A - Computacion", "Edificio principal"));
            Edificio b = edificios.save(new Edificio("B", "Edificio B - Aulas", "Aulas y laboratorios"));

            Zona z1 = new Zona(); z1.setNombre("Punto de reunion patio central");
            z1.setTipo(TipoZona.SEGURA); z1.setPiso(0); z1.setEdificio(a);
            Zona z2 = new Zona(); z2.setNombre("Sala de servidores");
            z2.setTipo(TipoZona.RIESGO); z2.setPiso(1); z2.setEdificio(a);
            Zona z3 = new Zona(); z3.setNombre("Aula 201");
            z3.setTipo(TipoZona.NORMAL); z3.setPiso(2); z3.setEdificio(b);
            zonas.save(z1); zonas.save(z2); zonas.save(z3);

            NodoFog n1 = new NodoFog();
            n1.setNombre("fog-edificioA"); n1.setEdificio(a);
            n1.setHost("127.0.0.1"); n1.setPuerto(8080);
            nodos.save(n1);

            // Usuarios de ejemplo con contraseñas iniciales
            // TODO Sprint-seguridad: cifrar contraseñas con AES-256 (R8)
            Usuario u1 = new Usuario(); u1.setIdentificador("admin-001");
            u1.setNombre("Administrador"); u1.setRol(Rol.ADMINISTRADOR);
            u1.setEdificio(a); u1.setPassword("admin123");

            Usuario u2 = new Usuario(); u2.setIdentificador("brig-001");
            u2.setNombre("Brigadista Juan"); u2.setRol(Rol.BRIGADISTA);
            u2.setEdificio(a); u2.setPassword("brig123");

            Usuario u3 = new Usuario(); u3.setIdentificador("usr-001");
            u3.setNombre("Usuaria Ana"); u3.setRol(Rol.USUARIO);
            u3.setEdificio(a); u3.setPassword("usr123");

            usuarios.save(u1); usuarios.save(u2); usuarios.save(u3);

            parametros.save(new Parametro("frecuencia_refresco_seg",  "3",    "Refresco del plano (QA-02)"));
            parametros.save(new Parametro("timeout_emergencia_min",   "60",   "Duracion maxima de emergencia activa"));
            parametros.save(new Parametro("frecuencia_heartbeat_seg", "30",   "Intervalo de heartbeat de la app victima"));
            parametros.save(new Parametro("max_usuarios_por_nodo",    "1000", "Limite de dispositivos por edificio (QA-08)"));
        };
    }
}
