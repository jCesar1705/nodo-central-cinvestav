package mx.cinvestav.central.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mx.cinvestav.central.model.TipoZona;

public class ZonaRequest {
    @NotBlank public String nombre;
    @NotNull  public TipoZona tipo;
    @NotNull  public Long edificioId;
    public Integer piso;
}
