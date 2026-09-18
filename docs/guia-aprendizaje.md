# Guia de Aprendizaje: SistemaZonaFit

## Un recorrido completo por los fundamentos de JDBC y arquitectura en capas

---

## Capitulo 1: La Clase Cliente (POJO / Entidad)

### 1.1. Que es un POJO?

**POJO** significa *Plain Old Java Object*. Es simplemente una clase Java comun y corriente que no hereda de ninguna clase especial ni implementa interfaces raras. Un POJO es la representacion en codigo de un objeto del mundo real.

En nuestro caso, un **Cliente** del gimnasio tiene estas propiedades:

| Campo       | Tipo     | Descripcion                        |
|-------------|----------|------------------------------------|
| `id`        | `int`    | Identificador unico en la BD       |
| `nombre`    | `String` | Nombre del cliente                 |
| `apellido`  | `String` | Apellido del cliente               |
| `membresia` | `int`    | Numero de membresia                |

Un POJO sirve como **contenedor de datos** que se transporta entre las capas de la aplicacion. Cuando hagas un `SELECT * FROM cliente`, cada fila de la base de datos se convierte en un objeto `Cliente` en memoria Java.

### 1.2. Encapsulamiento: Getters y Setters

La clase `Cliente` tiene sus atributos declarados como `private`. Esto significa que **ninguna otra clase puede acceder directamente** a esos campos. Para leer o modificarlos, se usan metodos publicos:

```java
// Getter - para LEER el valor
public String getNombre() {
    return nombre;
}

// Setter - para MODIFICAR el valor
public void setNombre(String nombre) {
    this.nombre = nombre;
}
```

**Por que `this.nombre = nombre`?**

La palabra reservada `this` se refiere al **objeto actual** (la instancia de la clase). Cuando el parametro se llama igual que el atributo, `this.nombre` resuelve la ambiguedad: "asignale al atributo `nombre` de **este** objeto el valor del parametro `nombre`".

Sin `this`, el codigo seria:

```java
public void setNombre(String nombre) {
    nombre = nombre;  // ERROR: el parametro "apaga" al atributo
}
```

### 1.3. Constructores

Java nos permite definir multiples constructores para crear objetos de distintas formas:

```java
public Cliente() {}                           // Constructor vacio
public Cliente(int id) { this.id = id; }      // Solo con ID
public Cliente(String nombre, String apellido, int membresia) { ... }  // Sin ID
public Cliente(int id, String nombre, String apellido, int membresia) { ... }  // Completo
```

El constructor **completo** (linea 25) usa `this(nombre, apellido, membresia)` para **reutilizar** el codigo del constructor de 3 parametros. Esto se llama **sobrecarga de constructores** y evita repetir logica.

### 1.4. toString(), equals() y hashCode()

Estos tres metodos son heredados de `Object` (la clase padre de todo en Java). Los sobreescribimos para darles un comportamiento util:

- **`toString()`**: Devuelve una representacion legible del objeto. Sin esto, imprimir un `Cliente` mostraria algo como `zona_fit.dominio.Cliente@1a2b3c` (direccion de memoria).
- **`equals()`**: Compara si dos objetos son "iguales". Por defecto, `==` compara si apuntan al **mismo lugar de memoria**, no si tienen los mismos datos. Sobreescribiendo `equals`, decimos que dos clientes son iguales si tienen el mismo `id`, `nombre`, `apellido` y `membresia`.
- **`hashCode()`**: Debe ser coherente con `equals`. Si dos objetos son iguales segun `equals`, deben tener el mismo `hashCode`. Esto es fundamental cuando uses objetos como claves en un `HashMap` o los guardes en un `HashSet`.

---

## Capitulo 2: Conexion a la Base de Datos (JDBC)

### 2.1. Que es JDBC?

**JDBC** (Java Database Connectivity) es la API estandar de Java para conectarse a bases de datos关系ales (MySQL, PostgreSQL, Oracle, etc.). Funciona mediante un sistema de **drivers**: cada base de datos tiene su propio driver que traduce las llamadas de Java a protocolos de red que el servidor de BD entiende.

El flujo basico es:

```
Tu codigo Java  -->  JDBC API  -->  Driver MySQL  -->  Servidor MySQL (localhost:3306)
```

### 2.2. Anatomia de la clase Conexion

```java
public static Connection getConexion() {
    Connection conexion = null;
    var baseDatos = "zona_fit_db";
    var url = "jdbc:mysql://localhost:3306/" + baseDatos;
    var usuario = "root";
    var password = "1234";
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conexion = DriverManager.getConnection(url, usuario, password);
    } catch (Exception e) {
        System.out.println("Error al conectarnos a la BD: " + e.getMessage());
    }
    return conexion;
}
```

Analicemos linea por linea:

#### `var` - Inferencia de tipos
```java
var baseDatos = "zona_fit_db";
```
`var` es una caracteristica introducida en Java 10. En lugar de escribir `String baseDatos = "zona_fit_db";`, el compilador **adivina** el tipo analizando la asignacion. Es solo azucar sintactico; el codigo compilado es identico.

#### `Class.forName("com.mysql.cj.jdbc.Driver")`
Esta linea **carga la clase del driver MySQL** en memoria. Cuando JVM ejecuta `Class.forName()`, busca en el classpath la clase indicada y la inicializa. El driver, al inicializarse, se **auto-registra** en `DriverManager` como manejador de URLs que empiezan con `jdbc:mysql://`.

**Nota:** En versiones modernas de JDBC (4.0+, Java 6+), esta linea ya no es estrictamente necesaria porque los drivers se cargan automaticamente via **SPI** (Service Provider Interface). Pero veras mucha gente escribirla por compatibilidad o costumbre.

#### `DriverManager.getConnection(url, usuario, password)`
Este es el metodo que **realmente abre la conexion**. Recibe:

- **url**: Sigue el formato `jdbc:protocolo://host:puerto/baseDatos`
- **usuario**: Credencial de acceso
- **password**: Contrasena

Devuelve un objeto `Connection` que representa la sesion abierta con el servidor de BD.

#### try/catch
La conexion puede fallar por multiples razones: MySQL no esta corriendo, credenciales incorrectas, la BD no existe, el puerto esta bloqueado, etc. El `catch` captura cualquier excepcion y evita que el programa crashee.

### 2.3. Por que es un metodo `static`?

```java
public static Connection getConexion()
```

`static` significa que el metodo **pertece a la clase**, no a una instancia. No necesitas crear un `new Conexion()` para usarlo. Lo llamas directamente:

```java
var conexion = Conexion.getConexion();
```

En este contexto, tiene sentido porque la conexion es un **servicio utilitario** que no depende de ningun estado interno de un objeto.

---

## Capitulo 3: La Interfaz IClienteDAO

### 3.1. Que es una interfaz en Java?

Una interfaz es un **contrato**. Define **que** metodos existen, pero no **como** se implementan. Es como un plano o un formulario en blanco: dice "esta funcionalidad debe existir", pero deja la implementacion a quien la escriba.

```java
public interface IClienteDAO {
    List<Cliente> listarClientes();
    boolean buscarClientePorId(Cliente cliente);
    boolean agregarCliente(Cliente cliente);
    boolean modificarCliente(Cliente cliente);
    boolean eliminarCliente(Cliente cliente);
}
```

La convencion de nombres `IClienteDAO` (con `I` al inicio) es comun en Java para indicar que es una **interfaz**. No es obligatorio, pero ayuda a distinguir la interfaz de su implementacion.

### 3.2. Por que programar contra una interfaz?

Este es uno de los principios mas importantes en diseno de software: **Programar contra una interfaz, no contra una implementacion**.

Imagina que hoy usas MySQL. Si manana quieres cambiar a PostgreSQL, solo necesitas crear una nueva clase `ClienteDAO` que implemente `IClienteDAO` pero conectandose a PostgreSQL. **Ningun otro codigo del proyecto cambia**, porque todo el resto del programa solo conoce a `IClienteDAO`, no a la implementacion concreta.

```
Main  -->  IClienteDAO (interfaz)
                |
                +-- ClienteDAO MySQL (implementacion concreta)
                +-- ClienteDAO PostgreSQL (implementacion alternativa)
```

En nuestro codigo, esto se ve asi:

```java
IClienteDAO clienteDAO = new ClienteDAO();  // La variable es del tipo interfaz
var clientes = clienteDAO.listarClientes();  // Se llama al metodo de la interfaz
```

### 3.3. Que es el patron DAO?

**DAO** significa *Data Access Object*. Es un patron de diseno que **aisla** toda la logica de acceso a bases de datos en una sola capa. La ventaja es que si cambias la forma en que accedes a los datos (de JDBC a JPA, de MySQL a MongoDB, etc.), solo modificas la capa DAO, sin tocar la logica de negocio ni la interfaz de usuario.

```
Capa de Presentacion (Main/Consola)
        |
Capa de Negocio / Logica
        |
Capa de Datos (DAO + JDBC)
        |
Base de Datos (MySQL)
```

---

## Capitulo 4: La Implementacion ClienteDAO

### 4.1. Analisis del metodo listarClientes()

Este es el metodo mas importante del proyecto porque es el unico implementado. Vamos paso a paso:

```java
public List<Cliente> listarClientes() {
    List<Cliente> clientes = new ArrayList<>();
    PreparedStatement ps;
    ResultSet rs;
    Connection con = getConexion();
    var sql = "SELECT * FROM cliente ORDER BY id";
    try {
        ps = con.prepareStatement(sql);
        rs = ps.executeQuery();
        while (rs.next()) {
            var cliente = new Cliente();
            cliente.setId(rs.getInt("id"));
            cliente.setNombre(rs.getString("nombre"));
            cliente.setApellido(rs.getString("apellido"));
            cliente.setMembresia(rs.getInt("membresia"));
            clientes.add(cliente);
        }
    } catch (Exception e) {
        System.out.println("Error al listar clientes: " + e.getMessage());
    } finally {
        try {
            con.close();
        } catch (Exception e) {
            System.out.println("Error al cerrar conexion.");
        }
    }
    return clientes;
}
```

#### Paso 1: Preparar la estructura de retorno
```java
List<Cliente> clientes = new ArrayList<>();
```
Creamos una lista vacia que al final tendra todos los clientes que vengan de la BD.

#### Paso 2: Abrir la conexion
```java
Connection con = getConexion();
```
Usamos nuestro metodo estatico de la clase `Conexion`. Esto abre una sesion TCP con MySQL en el puerto 3306.

#### Paso 3: Preparar la consulta
```java
var sql = "SELECT * FROM cliente ORDER BY id";
ps = con.prepareStatement(sql);
```

**`PreparedStatement` vs `Statement`:**

- `Statement`: Ejecuta SQL cruda. Es **vulnerable a SQL Injection** porque concatena strings directamente.
- `PreparedStatement`: Pre-compila la consulta SQL en el servidor. Es mas seguro y mas rapido si ejecutas la misma consulta multiples veces.

Un ejemplo de SQL Injection (lo que `PreparedStatement` previene):
```java
// PELIGROSO con Statement:
String sql = "SELECT * FROM cliente WHERE id = " + userInput;
// Si userInput es "1 OR 1=1", borraria TODA la tabla con DELETE

// SEGURO con PreparedStatement:
String sql = "SELECT * FROM cliente WHERE id = ?";
ps.setInt(1, userInput);  // Java escapa los caracteres peligrosos
```

En nuestro caso no hay parametros (`?`), pero se usa `PreparedStatement` como buena practica.

#### Paso 4: Ejecutar la consulta
```java
rs = ps.executeQuery();
```
`executeQuery()` ejecuta el `SELECT` y devuelve un **`ResultSet`**, que es como un cursor o un puntero que recorre las filas del resultado.

#### Paso 5: Recorrer los resultados
```java
while (rs.next()) {
    var cliente = new Cliente();
    cliente.setId(rs.getInt("id"));
    cliente.setNombre(rs.getString("nombre"));
    cliente.setApellido(rs.getString("apellido"));
    cliente.setMembresia(rs.getInt("membresia"));
    clientes.add(cliente);
}
```

`rs.next()` hace dos cosas:
1. **Mueve el cursor** a la siguiente fila
2. **Devuelve `true`** si hay una fila, `false` si ya no hay mas

En cada iteracion, se crea un nuevo `Cliente`, se le asignan los valores de las columnas usando `rs.getInt()` y `rs.getString()` (pasando el **nombre de la columna** como aparece en la BD), y se agrega a la lista.

#### Paso 6: Cerrar la conexion
```java
finally {
    try {
        con.close();
    } catch (Exception e) {
        System.out.println("Error al cerrar conexion.");
    }
}
```

El bloque `finally` **siempre se ejecuta**, tanto si todo salio bien como si hubo una excepcion. Esto garantiza que la conexion se cierre y no quede abierta consumiendo recursos del servidor.

**Nota importante:** En proyectos reales, se usa `try-with-resources` para gestionar esto automaticamente:

```java
try (Connection con = getConexion();
     PreparedStatement ps = con.prepareStatement(sql);
     ResultSet rs = ps.executeQuery()) {
    // ...
}  // Se cierran automaticamente en orden inverso
```

Esto es mas limpio y evita olvidar cerrar algo.

### 4.2. Metodos no implementados

Los metodos `agregarCliente`, `modificarCliente`, `eliminarCliente` y `buscarClientePorId` todavia devuelven `false`. Su implementacion seguiria el mismo patron de `listarClientes()`, pero usando `executeUpdate()` en lugar de `executeQuery()` (porque INSERT/UPDATE/DELETE no devuelven un `ResultSet`, sino un `int` con la cantidad de filas afectadas):

```java
public boolean agregarCliente(Cliente cliente) {
    PreparedStatement ps;
    Connection con = getConexion();
    var sql = "INSERT INTO cliente(nombre, apellido, membresia) VALUES(?, ?, ?)";
    try {
        ps = con.prepareStatement(sql);
        ps.setString(1, cliente.getNombre());
        ps.setString(2, cliente.getApellido());
        ps.setInt(3, cliente.getMembresia());
        ps.executeUpdate();
        return true;
    } catch (Exception e) {
        System.out.println("Error al agregar cliente: " + e.getMessage());
        return false;
    } finally {
        try { con.close(); } catch (Exception e) {}
    }
}
```

Los `?` son **parametros posicionales** que se llenan con `ps.setXxx(posicion, valor)`.

---

## Capitulo 5: Conceptos Transversales

### 5.1. Estructura de Paquetes (Arquitectura en Capas)

El proyecto esta organizado en paquetes que representan capas:

```
zona_fit/
    dominio/       --> Entidades de negocio (Cliente)
    conexion/      --> Configuracion de BD (Conexion)
    datos/         --> Acceso a datos (IClienteDAO, ClienteDAO)
```

Esta separacion no es arbitraria. Cada paquete tiene una **responsabilidad unica**:

- Si cambias la estructura de `Cliente`, solo tocas `dominio/`
- Si cambias de MySQL a PostgreSQL, solo tocas `conexion/` y `datos/`
- Si agregas validaciones de negocio, creas un paquete `negocio/`

Esto se conoce como **Separacion de Responsabilidades** (SRP - Single Responsibility Principle).

### 5.2. Method References y Lambdas

En el `main` de `ClienteDAO`:

```java
clientes.forEach(System.out::println);
```

Esto es equivalente a:

```java
for (Cliente c : clientes) {
    System.out.println(c);
}
```

`System.out::println` es un **method reference** (referencia a metodo). Es una forma abreviada de escribir una lambda:

```java
clientes.forEach(cliente -> System.out.println(cliente));
```

Ambas formas son funcionalmente identicas; es cuestion de estilo.

---

## Resumen Visual del Flujo del Programa

```
1. Main crea un ClienteDAO
         |
2. ClienteDAO llama a getConexion()
         |
3. Conexion abre sesion con MySQL via JDBC
         |
4. ClienteDAO crea PreparedStatement con SQL
         |
5. Se ejecuta executeQuery() y se obtiene ResultSet
         |
6. Se recorre ResultSet, creando objetos Cliente
         |
7. La lista de Clientes se retorna al Main
         |
8. Main imprime los clientes con forEach
         |
9. La conexion se cierra en el bloque finally
```

---

## Conceptos Clave para Recordar

| Concepto | Que es | Para que sirve |
|----------|--------|----------------|
| POJO | Clase Java simple con atributos y metodos basicos | Representar entidades del mundo real |
| JDBC | API estandar de Java para bases de datos | Conectar, consultar y modificar datos en BD |
| PreparedStatement | Consulta SQL pre-compilada con parametros | Seguridad (evita SQL Injection) y rendimiento |
| ResultSet | Cursor que recorre los resultados de un SELECT | Extraer datos fila por fila |
| Interfaz | Contrato que define metodos sin implementar | Desacoplamiento y flexibilidad |
| DAO | Patron de diseno para acceso a datos | Aislar la logica de BD del resto de la app |
| Patrón en capas | Organizar el codigo por responsabilidades | Mantenibilidad y escalabilidad |
| finally | Bloque que siempre se ejecuta | Liberar recursos (cerrar conexiones) |
| this | Referencia al objeto actual | Resolver ambiguedades entre atributos y parametros |
| var | Inferencia de tipos (Java 10+) | Codigo mas conciso sin perder type-safety |

