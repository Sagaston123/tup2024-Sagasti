# Sistema Bancario extendido a prestamos 2025
# Para ejecutarlo, primero correr "mvn clean install". 
# Luego "mvn spring-boot:run". O click en RUN desde la clase Application.java

## Endpoints de clientes

### Crear cliente
- **POST `/cliente`**
- Crea un nuevo cliente.
- **Body:** `ClienteDto` (JSON con datos personales)
- **Respuestas:**  
  - 200 OK: Cliente creado  
  - 400/409: Error de validación o cliente ya existe

### Consultar cliente
- **GET `/cliente/{dni}`**
- Devuelve los datos del cliente.
- **Respuestas:**  
  - 200 OK: Datos del cliente  
  - 404: Cliente no encontrado

### Listar todos los clientes
- **GET `/cliente`**
- Devuelve la lista de todos los clientes del sistema.

### Crear cuenta
- **POST `/cuenta`**
- Crea una nueva cuenta bancaria para un cliente existente.
- **Body:** `CuentaDto` (JSON con datos de la cuenta y el DNI del cliente)
- **Respuestas:**  
  - 200 OK: Cuenta creada  
  - 400/409: Error de validación o cuenta/cliente ya existe

## Endpoints de cuentas

### Consultar cuenta
- **GET `/cuenta/{numeroCuenta}`**
- Devuelve los datos de la cuenta y su titular.
- **Respuestas:**  
  - 200 OK: Datos de la cuenta  
  - 404: Cuenta no encontrada

### Depositar dinero
- **PUT `/cuenta/{numeroCuenta}/depositar?monto={monto}`**
- Deposita dinero en la cuenta indicada.
- **Respuestas:**  
  - 200 OK: Mensaje de éxito  
  - 400: Monto negativo  
  - 404: Cuenta no encontrada

### Extraer dinero
- **PUT `/cuenta/{numeroCuenta}/extraer?monto={monto}`**
- Extrae dinero de la cuenta indicada.
- **Respuestas:**  
  - 200 OK: Mensaje de éxito  
  - 400: Monto negativo o saldo insuficiente  
  - 404: Cuenta no encontrada

### Transferir dinero
- **PUT `/cuenta/transferir?origen={origen}&destino={destino}&monto={monto}`**
- Transfiere dinero entre dos cuentas.
- **Respuestas:**  
  - 200 OK: Mensaje de éxito  
  - 400: Monto negativo, saldo insuficiente o tipos incompatibles  
  - 404: Cuenta no encontrada

### Listar todas las cuentas
- **GET `/cuenta`**
- Devuelve la lista de todas las cuentas del sistema.