package mx.cinvestav.central.config;

import java.lang.annotation.*;

/**
 * Marca un endpoint que solo puede ejecutar el rol ADMINISTRADOR.
 * El RolInterceptor revisa el header X-Role en cada peticion marcada.
 * (En Sprint 3 esto se reemplaza por JWT firmado, igual que en el nodo FOG.)
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SoloAdmin { }
