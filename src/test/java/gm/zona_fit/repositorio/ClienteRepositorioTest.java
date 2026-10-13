package gm.zona_fit.repositorio;

import gm.zona_fit.modelo.Cliente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ClienteRepositorioTest {

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Test
    void guardarYBuscarClientePorId() {
        Cliente guardado = clienteRepositorio.save(new Cliente(null, "Ana", "Torres", 12));

        Optional<Cliente> resultado = clienteRepositorio.findById(guardado.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Ana");
        assertThat(resultado.get().getApellido()).isEqualTo("Torres");
        assertThat(resultado.get().getMembresia()).isEqualTo(12);
    }

    @Test
    void eliminarCliente() {
        Cliente guardado = clienteRepositorio.save(new Cliente(null, "Ana", "Torres", 12));

        clienteRepositorio.delete(guardado);

        assertThat(clienteRepositorio.findById(guardado.getId())).isEmpty();
    }
}
