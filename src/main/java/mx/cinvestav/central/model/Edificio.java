package mx.cinvestav.central.model;

import jakarta.persistence.*;

/** Edificio de la IES. Cada edificio tendra (idealmente) un nodo FOG. */
@Entity
@Table(name = "edificio")
public class Edificio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Clave corta y unica, ej. "A", "B", "COMP". */
    @Column(nullable = false, unique = true, length = 16)
    private String clave;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 512)
    private String descripcion;

    public Edificio() { }

    public Edificio(String clave, String nombre, String descripcion) {
        this.clave = clave;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
