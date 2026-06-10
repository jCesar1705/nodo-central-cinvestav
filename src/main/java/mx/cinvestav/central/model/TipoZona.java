package mx.cinvestav.central.model;

/** Clasificacion de una zona dentro de un edificio. */
public enum TipoZona {
    SEGURA,   // punto de reunion / zona segura (se muestra en CU-01)
    NORMAL,   // espacio comun (aula, pasillo, oficina)
    RIESGO    // zona identificada como peligrosa
}
