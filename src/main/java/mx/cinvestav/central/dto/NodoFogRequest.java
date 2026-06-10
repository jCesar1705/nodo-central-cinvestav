package mx.cinvestav.central.dto;

import jakarta.validation.constraints.*;

public class NodoFogRequest {
    @NotBlank public String nombre;
    @NotNull  public Long edificioId;
    @NotBlank public String host;
    @NotNull @Positive public Integer puerto;
}
