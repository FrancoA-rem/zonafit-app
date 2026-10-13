package gm.zona_fit.controlador;

import gm.zona_fit.modelo.Cliente;
import gm.zona_fit.servicio.IClienteServicio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IClienteServicio clienteServicio;

    @Test
    void listarClientesRetornaLista() throws Exception {
        when(clienteServicio.listarClientes()).thenReturn(List.of(
                new Cliente(1, "Ana", "Torres", 12)
        ));

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Ana"))
                .andExpect(jsonPath("$[0].apellido").value("Torres"))
                .andExpect(jsonPath("$[0].membresia").value(12));
    }

    @Test
    void buscarClientePorIdRetornaCliente() throws Exception {
        when(clienteServicio.buscarClientePorId(1))
                .thenReturn(new Cliente(1, "Ana", "Torres", 12));

        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Ana"))
                .andExpect(jsonPath("$.apellido").value("Torres"))
                .andExpect(jsonPath("$.membresia").value(12));
    }

    @Test
    void buscarClientePorIdInexistenteRetornaNotFound() throws Exception {
        when(clienteServicio.buscarClientePorId(99)).thenReturn(null);

        mockMvc.perform(get("/api/clientes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearClienteRetornaCreated() throws Exception {
        doAnswer(invocation -> {
            Cliente cliente = invocation.getArgument(0);
            cliente.setId(10);
            return null;
        }).when(clienteServicio).guardarCliente(any(Cliente.class));

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Ana",
                                  "apellido": "Torres",
                                  "membresia": 12
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("Ana"))
                .andExpect(jsonPath("$.apellido").value("Torres"))
                .andExpect(jsonPath("$.membresia").value(12));

        verify(clienteServicio).guardarCliente(any(Cliente.class));
    }

    @Test
    void crearClienteConDatosInvalidosRetornaBadRequest() throws Exception {
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "",
                                  "apellido": "Torres",
                                  "membresia": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errores.nombre").value("El nombre es obligatorio"))
                .andExpect(jsonPath("$.errores.membresia").value("La membresía debe ser mayor a cero"));
    }

    @Test
    void actualizarClienteRetornaOk() throws Exception {
        when(clienteServicio.buscarClientePorId(1))
                .thenReturn(new Cliente(1, "Ana", "Torres", 12));

        mockMvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Ana Maria",
                                  "apellido": "Torres",
                                  "membresia": 24
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Ana Maria"))
                .andExpect(jsonPath("$.apellido").value("Torres"))
                .andExpect(jsonPath("$.membresia").value(24));

        verify(clienteServicio).guardarCliente(any(Cliente.class));
    }

    @Test
    void actualizarClienteInexistenteRetornaNotFound() throws Exception {
        when(clienteServicio.buscarClientePorId(99)).thenReturn(null);

        mockMvc.perform(put("/api/clientes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Ana Maria",
                                  "apellido": "Torres",
                                  "membresia": 24
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarClienteRetornaNoContent() throws Exception {
        when(clienteServicio.buscarClientePorId(1))
                .thenReturn(new Cliente(1, "Ana", "Torres", 12));

        mockMvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isNoContent());

        verify(clienteServicio).eliminarCliente(any(Cliente.class));
    }

    @Test
    void eliminarClienteInexistenteRetornaNotFound() throws Exception {
        when(clienteServicio.buscarClientePorId(99)).thenReturn(null);

        mockMvc.perform(delete("/api/clientes/99"))
                .andExpect(status().isNotFound());
    }
}
