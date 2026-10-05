package gm.zona_fit.dto;

public class ClienteResponse {
    private Integer id;
    private String nombre;
    private String apellido;
    private Integer membresia;


    public ClienteResponse(){

    }

    public ClienteResponse(Integer id, String nombre, String apellido, Integer membresia) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.membresia = membresia;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    //Esto es lo que se devuelve a consultar
}
