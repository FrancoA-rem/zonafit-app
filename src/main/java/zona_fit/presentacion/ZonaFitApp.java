package zona_fit.presentacion;

import zona_fit.datos.ClienteDAO;
import zona_fit.datos.IClienteDAO;
import zona_fit.dominio.Cliente;

import java.util.Scanner;

public class ZonaFitApp {
    public static void main(String[] args) {
        zonaFitApp();
    }

    private static void zonaFitApp(){
        boolean salir = false;
        Scanner teclado = new Scanner(System.in);
        ClienteDAO clienteDAO = new ClienteDAO();
        while (!salir){
            try {
                int opcion = mostrarMenu(teclado);
                salir = ejecutarOpciones(teclado,opcion, clienteDAO);
            }catch (Exception e){
                System.out.println("Error al ejecutar opciones : " + e.getMessage());
            }
            System.out.println("");
        }
    }

    private static int mostrarMenu(Scanner teclado){
        System.out.print("""
                *** Zona Fit (GYM)
                1. Listar Clientes
                2. Buscar Clienes
                3. Agregar Clientes
                4. Modificar Clientes
                5. Eliminar Clientes
                6. Salir
                Elije una opcion:\s""");
        int opcion = Integer.parseInt(teclado.nextLine());
        return opcion;
    }

    private static boolean ejecutarOpciones(Scanner teclado, int opcion, IClienteDAO clienteDAO){
        boolean salir = false;
        switch (opcion){
            case 1:
                System.out.println("Listado de clientes");
                var clientes = clienteDAO.listarClientes();
                clientes.forEach(System.out::println);
                break;
            case 2:
                System.out.println("Introduce el ID del cliente a buscar: ");
                var idCliente = Integer.parseInt(teclado.nextLine());
                var cliente = new Cliente(idCliente);
                var encontrado = clienteDAO.buscarClientePorId(cliente);
                if (encontrado){
                    System.out.println("Cliente encontrado: " + cliente);
                } else{
                    System.out.println("Cliente no encontrado");
                }
                break;
            case 3:
                System.out.println("Agregar cliente");
                System.out.println("Nombre: ");
                var nombre = teclado.nextLine();
                System.out.println("Apellido: ");
                var apellido = teclado.nextLine();
                System.out.println("Membresia: ");
                var membresia = Integer.parseInt(teclado.nextLine());

                Cliente clienteAgregado = new Cliente(nombre, apellido, membresia);
                var agregado = clienteDAO.agregarCliente(clienteAgregado);
                if (agregado){
                    System.out.println("Cliente agregado: " + clienteAgregado);
                } else{
                    System.out.println("Cliente no agregado: " + clienteAgregado);
                }
                break;
            case 4:
                System.out.println("Modificar cliente");
                System.out.println("Id cliente: ");
                var idClienteMod = Integer.parseInt(teclado.nextLine());
                System.out.println("Nombre: ");
                var nombreMod = teclado.nextLine();
                System.out.println("Apellido: ");
                var apellidoMod = teclado.nextLine();
                System.out.println("Membresia: ");
                var membresiaMod = Integer.parseInt(teclado.nextLine());

                Cliente clienteMod = new Cliente(idClienteMod, nombreMod, apellidoMod, membresiaMod);
                boolean modificado = clienteDAO.modificarCliente(clienteMod);
                if (modificado){
                    System.out.println("Cliente modificado: " + clienteMod);
                } else{
                    System.out.println("Cliente no modificado: " + clienteMod);
                }
                break;
            case 5:
                System.out.println("Introduce el ID del cliente a eliminar: ");
                var idClienteEliminar = Integer.parseInt(teclado.nextLine());
                var clienteEliminar = new Cliente(idClienteEliminar);
                var eliminado = clienteDAO.eliminarCliente(clienteEliminar);
                if (eliminado){
                    System.out.println("Cliente eliminado: " + clienteEliminar);
                } else{
                    System.out.println("Cliente no eliminado");
                }
                break;
            case 6:
                System.out.println("Hasta luego");
                salir = true;
                break;
            default:
                System.out.println("Opcion no reconocida: " + opcion);
                break;

        }
        return salir;
    }
}
