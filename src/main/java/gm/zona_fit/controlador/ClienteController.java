package gm.zona_fit.controlador;

import gm.zona_fit.dto.ClienteRequest;
import gm.zona_fit.dto.ClienteResponse;
import gm.zona_fit.modelo.Cliente;
import gm.zona_fit.servicio.IClienteServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<ClienteResponse> crear(@RequestBody ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNombre(request.getNombre());
        cliente.setApellido(request.getApellido());
        cliente.setMembresia(request.getMembresia());

        clienteServicio.guardarCliente(cliente);

        ClienteResponse response = new ClienteResponse(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getMembresia()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable Integer id){
        Cliente cliente = clienteServicio.buscarClientePorId(id);

        if(cliente == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new ClienteResponse(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getMembresia()
        ));

    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizar(@PathVariable Integer id, @RequestBody ClienteRequest request) {
        Cliente cliente = clienteServicio.buscarClientePorId(id);

        if(cliente == null){
            return ResponseEntity.notFound().build();
        }

        cliente.setNombre(request.getNombre());
        cliente.setApellido(request.getApellido());
        cliente.setMembresia(request.getMembresia());
        clienteServicio.guardarCliente(cliente);

        return ResponseEntity.ok(new ClienteResponse(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getMembresia()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id){
        Cliente cliente = clienteServicio.buscarClientePorId(id);

        if(cliente == null){
            return ResponseEntity.notFound().build();
        }

        clienteServicio.eliminarCliente(cliente);
        return ResponseEntity.noContent().build();

    }

}
