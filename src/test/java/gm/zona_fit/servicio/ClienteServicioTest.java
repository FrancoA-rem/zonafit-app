package gm.zona_fit.servicio;

import gm.zona_fit.modelo.Cliente;
import gm.zona_fit.repositorio.ClienteRepositorio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServicioTest {

    @Mock
    private ClienteRepositorio clienteRepositorio;

    @InjectMocks
    private ClienteServicio clienteServicio;

    @Test
    void listarClientesRetornaLista() {
        List<Cliente> clientes = List.of(
                new Cliente(1, "Ana", "Torres", 12)
        );
        when(clienteRepositorio.findAll()).thenReturn(clientes);

        List<Cliente> resultado = clienteServicio.listarClientes();

        assertThat(resultado).isEqualTo(clientes);
        verify(clienteRepositorio).findAll();
    }

    @Test
    void buscarClientePorIdRetornaCliente() {
        Cliente cliente = new Cliente(1, "Ana", "Torres", 12);
        when(clienteRepositorio.findById(1)).thenReturn(Optional.of(cliente));

        Cliente resultado = clienteServicio.buscarClientePorId(1);

        assertThat(resultado).isEqualTo(cliente);
        verify(clienteRepositorio).findById(1);
    }

    @Test
    void buscarClientePorIdInexistenteRetornaNull() {
        when(clienteRepositorio.findById(99)).thenReturn(Optional.empty());

        Cliente resultado = clienteServicio.buscarClientePorId(99);

        assertThat(resultado).isNull();
        verify(clienteRepositorio).findById(99);
    }

    @Test
    void guardarClienteDelegaEnElRepositorio() {
        Cliente cliente = new Cliente(1, "Ana", "Torres", 12);

        clienteServicio.guardarCliente(cliente);

        verify(clienteRepositorio).save(cliente);
    }

    @Test
    void eliminarClienteDelegaEnElRepositorio() {
        Cliente cliente = new Cliente(1, "Ana", "Torres", 12);

        clienteServicio.eliminarCliente(cliente);

        verify(clienteRepositorio).delete(cliente);
    }
}
