package mx.cinvestav.central.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String identificador;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Rol rol = Rol.VICTIMA;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "edificio_id")
    private Edificio edificio;

    @Column(nullable = false)
    private boolean activo = true;

    /**
     * Contraseña en texto plano.
     * TODO Sprint-seguridad: cifrar con AES-256 antes de guardar (R8).
     */
    @Column(nullable = false)
    private String password = "";

    public Usuario() {}

    public Long getId()                    { return id; }
    public void setId(Long id)             { this.id = id; }
    public String getIdentificador()       { return identificador; }
    public void setIdentificador(String s) { this.identificador = s; }
    public String getNombre()              { return nombre; }
    public void setNombre(String n)        { this.nombre = n; }
    public Rol getRol()                    { return rol; }
    public void setRol(Rol r)             { this.rol = r; }
    public Edificio getEdificio()          { return edificio; }
    public void setEdificio(Edificio e)   { this.edificio = e; }
    public boolean isActivo()              { return activo; }
    public void setActivo(boolean a)       { this.activo = a; }
    public String getPassword()            { return password; }
    public void setPassword(String p)      { this.password = p; }
}
