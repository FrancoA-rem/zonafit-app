package gm.zona_fit.controlador;

import gm.zona_fit.dto.ClienteResponse;
import gm.zona_fit.modelo.Cliente;
import gm.zona_fit.servicio.IClienteServicio;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final IClienteServicio clienteServicio;

    public ClienteController(IClienteServicio clienteServicio){
        this.clienteServicio = clienteServicio;
    }

    @GetMapping
    public List<ClienteResponse> listarClientes() {
        List<ClienteResponse> respuesta = new ArrayList<>();

        for (Cliente cliente : clienteServicio.listarClientes()) {
            respuesta.add(new ClienteResponse(
                    cliente.getId(),
                    cliente.getNombre(),
                    cliente.getApellido(),
                    cliente.getMembresia()
            ));
        }

        return respuesta;
    }


}
