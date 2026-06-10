package mx.cinvestav.central.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mx.cinvestav.central.model.Rol;

public class UsuarioRequest {
    @NotBlank public String identificador;
    @NotBlank public String nombre;
    @NotNull  public Rol    rol;
    public Long    edificioId;
    public Boolean activo;
    /** Contraseña en texto plano. Opcional en edición (vacío = no cambiar). */
    public String  password;
}
