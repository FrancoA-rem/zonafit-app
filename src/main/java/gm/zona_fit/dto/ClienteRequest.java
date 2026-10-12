package gm.zona_fit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public class ClienteRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotNull(message = "La membresía es obligatoria")
    @Positive(message = "La membresía debe ser mayor a cero")
    private Integer membresia;

    public ClienteRequest() {
    }

    public ClienteRequest(String nombre, String apellido, Integer membresia) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.membresia = membresia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Integer getMembresia() {
        return membresia;
    }

    public void setMembresia(Integer membresia) {
        this.membresia = membresia;
    }

}
