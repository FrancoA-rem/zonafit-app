package gm.zona_fit.dto;

public class ClienteRequest {
    private String nombre;
    private String apellido;
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
