package mx.cinvestav.central.model;

/** Estado de conectividad de un nodo FOG visto desde el nodo central. */
public enum EstadoNodo {
    ONLINE,       // respondio en la ultima sincronizacion
    OFFLINE,      // no respondio (config quedo pendiente de aplicar)
    DESCONOCIDO   // aun no se ha intentado sincronizar
}
