package mx.cinvestav.central.config;

import mx.cinvestav.central.model.*;
import mx.cinvestav.central.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner seed(EdificioRepository edificios, ZonaRepository zonas,
                           NodoFogRepository nodos, UsuarioRepository usuarios,
                           ParametroRepository parametros,
                           BCryptPasswordEncoder encoder) {
        return args -> {
            if (edificios.count() > 0) {
                // Re-hashear contraseñas planas que aún no estén cifradas
                for (Usuario u : usuarios.findAll()) {
                    String pwd = u.getPassword();
                    if (pwd != null && !pwd.startsWith("$2a$")) {
                        u.setPassword(encoder.encode(pwd));
                        usuarios.save(u);
                    }
                }
                return;
            }

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

            Usuario u1 = new Usuario(); u1.setIdentificador("admin-001");
            u1.setNombre("Administrador"); u1.setRol(Rol.ADMINISTRADOR);
            u1.setEdificio(a); u1.setPassword(encoder.encode("admin123"));

            Usuario u2 = new Usuario(); u2.setIdentificador("brig-001");
            u2.setNombre("Brigadista Juan"); u2.setRol(Rol.BRIGADISTA);
            u2.setEdificio(a); u2.setPassword(encoder.encode("brig123"));

            Usuario u3 = new Usuario(); u3.setIdentificador("usr-001");
            u3.setNombre("Usuaria Ana"); u3.setRol(Rol.VICTIMA);
            u3.setEdificio(a); u3.setPassword(encoder.encode("usr123"));

            // Usuarios que coinciden con el DataInitializer del nodo FOG
            Usuario u4 = new Usuario(); u4.setIdentificador("A07654321");
            u4.setNombre("Usuario FOG A07654321"); u4.setRol(Rol.BRIGADISTA);
            u4.setEdificio(a); u4.setPassword(encoder.encode("1234"));

            Usuario u5 = new Usuario(); u5.setIdentificador("A01234567");
            u5.setNombre("Usuario FOG A01234567"); u5.setRol(Rol.VICTIMA);
            u5.setEdificio(a); u5.setPassword(encoder.encode("1234"));

            Usuario u6 = new Usuario(); u6.setIdentificador("EXT-001");
            u6.setNombre("Usuario FOG EXT-001"); u6.setRol(Rol.VICTIMA);
            u6.setEdificio(a); u6.setPassword(encoder.encode("1234"));

            usuarios.save(u1); usuarios.save(u2); usuarios.save(u3);
            usuarios.save(u4); usuarios.save(u5); usuarios.save(u6);

            parametros.save(new Parametro("frecuencia_refresco_seg",  "3",    "Refresco del plano (QA-02)"));
            parametros.save(new Parametro("timeout_emergencia_min",   "60",   "Duracion maxima de emergencia activa"));
            parametros.save(new Parametro("frecuencia_heartbeat_seg", "30",   "Intervalo de heartbeat de la app victima"));
            parametros.save(new Parametro("max_usuarios_por_nodo",    "1000", "Limite de dispositivos por edificio (QA-08)"));
        };
    }
}
