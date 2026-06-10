package mx.cinvestav.central.model;

import jakarta.persistence.*;

/** Zona dentro de un edificio (aula, pasillo, punto de reunion...). */
@Entity
@Table(name = "zona")
public class Zona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private TipoZona tipo = TipoZona.NORMAL;

    /** Edificio al que pertenece la zona. */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "edificio_id", nullable = false)
    private Edificio edificio;

    private Integer piso;

    public Zona() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public TipoZona getTipo() { return tipo; }
    public void setTipo(TipoZona tipo) { this.tipo = tipo; }

    public Edificio getEdificio() { return edificio; }
    public void setEdificio(Edificio edificio) { this.edificio = edificio; }

    public Integer getPiso() { return piso; }
    public void setPiso(Integer piso) { this.piso = piso; }
}
