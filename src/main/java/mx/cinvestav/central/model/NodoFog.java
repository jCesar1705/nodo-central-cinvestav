package mx.cinvestav.central.model;

import jakarta.persistence.*;
import java.time.Instant;

/** Representa, en el registro central, a un nodo FOG fisico de un edificio. */
@Entity
@Table(name = "nodo_fog")
public class NodoFog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "edificio_id", nullable = false)
    private Edificio edificio;

    /** Direccion del nodo FOG en la red local, ej. "192.168.1.50". */
    @Column(nullable = false)
    private String host;

    @Column(nullable = false)
    private Integer puerto = 8080;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private EstadoNodo estado = EstadoNodo.DESCONOCIDO;

    /** Momento de la ultima sincronizacion exitosa de config hacia este nodo. */
    private Instant ultimaSincronizacion;

    public NodoFog() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Edificio getEdificio() { return edificio; }
    public void setEdificio(Edificio edificio) { this.edificio = edificio; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public Integer getPuerto() { return puerto; }
    public void setPuerto(Integer puerto) { this.puerto = puerto; }

    public EstadoNodo getEstado() { return estado; }
    public void setEstado(EstadoNodo estado) { this.estado = estado; }

    public Instant getUltimaSincronizacion() { return ultimaSincronizacion; }
    public void setUltimaSincronizacion(Instant t) { this.ultimaSincronizacion = t; }

    /** URL base del nodo, util para las llamadas de sincronizacion. */
    @Transient
    public String getBaseUrl() {
        return "http://" + host + ":" + puerto;
    }
}
