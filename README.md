# Sistema Bancario extendido a prestamos 2025

Para ejecutarlo, primero correr `mvn clean install`. Luego `mvn spring-boot:run`. O click en RUN desde la clase Application.java

## API Endpoints

### 📋 Clientes

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/cliente` | Crear un nuevo cliente |
| GET | `/cliente/{dni}` | Buscar cliente por DNI |
| GET | `/cliente` | Listar todos los clientes |

### 💰 Cuentas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/cuenta` | Crear nueva cuenta bancaria |
| GET | `/cuenta/{numeroCuenta}` | Consultar cuenta por número |
| GET | `/cuenta` | Listar todas las cuentas |
| PUT | `/cuenta/{numeroCuenta}/depositar?monto={valor}` | Depositar dinero en cuenta |
| PUT | `/cuenta/{numeroCuenta}/extraer?monto={valor}` | Extraer dinero de cuenta |
| PUT | `/cuenta/transferir?origen={numOrigen}&destino={numDestino}&monto={valor}` | Transferir dinero entre cuentas |

### 🏦 Préstamos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/prestamo` | Solicitar un nuevo préstamo |
| GET | `/prestamo/{clienteId}` | Consultar préstamos de un cliente |

## Endpoints de clientes

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/cliente` | Crear un nuevo cliente |
| GET | `/cliente/{dni}` | Buscar cliente por DNI |
| GET | `/cliente` | Listar todos los clientes |

## Endpoints de cuentas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/cuenta` | Crear nueva cuenta bancaria |
| GET | `/cuenta/{numeroCuenta}` | Consultar cuenta por número |
| GET | `/cuenta` | Listar todas las cuentas |
| PUT | `/cuenta/{numeroCuenta}/depositar?monto={valor}` | Depositar dinero en cuenta |
| PUT | `/cuenta/{numeroCuenta}/extraer?monto={valor}` | Extraer dinero de cuenta |
| PUT | `/cuenta/transferir?origen={numOrigen}&destino={numDestino}&monto={valor}` | Transferir dinero entre cuentas |

## Endpoints de préstamos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/prestamo` | Solicitar un nuevo préstamo |
| GET | `/prestamo/{clienteId}` | Consultar préstamos de un cliente |