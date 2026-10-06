# Calzado Store

Aplicación de consola para gestionar una tienda de calzado. El proyecto modela el recorrido principal de una compra: explorar productos y variantes, armar un carrito, confirmar una venta y consultar su estado. También incluye herramientas de administración para mantener el catálogo y hacer seguimiento de las ventas.

Está desarrollado en Java 21 y organizado en capas para separar la interfaz, las reglas de negocio y el acceso a los datos. Actualmente los datos se mantienen en memoria; al iniciar, se cargan registros de demostración para recorrer las funciones sin configuración adicional.

## Funcionalidades

### Experiencia del cliente

- Crear una cuenta o iniciar sesión.
- Consultar productos y variantes disponibles con color, talle, público, descripción, precio y stock.
- Agregar variantes al carrito y modificar cantidades, quitar artículos o vaciarlo.
- Iniciar una compra indicando dirección de envío y medio de pago.
- Consultar las compras propias, revisar su detalle y cancelar una compra activa.
- Consultar y actualizar la información de la propia cuenta.

### Herramientas de administración

- Crear y editar productos y sus variantes.
- Mantener colores del catálogo.
- Consultar todas las ventas, abrir su detalle y actualizar notas, estado de venta y estado de pago.
- Crear cuentas de administrador.

El acceso a las ventas y a las funciones de administración está protegido por rol. Cada cliente consulta sus propios carritos y compras; el administrador puede revisar ventas de todos los clientes.

## Recorrido de compra

1. El cliente elige un producto y una variante activa.
2. Agrega una cantidad al carrito. La cantidad no puede superar el stock disponible; si vuelve a agregar una variante que ya estaba en el carrito, se actualiza su cantidad sin superar el stock.
3. Al iniciar la compra, informa la dirección de envío y selecciona un medio de pago.
4. El sistema crea la venta y conserva en sus artículos una referencia descriptiva de la variante, su SKU, precio y cantidad. Calcula subtotal y total, y descuenta las unidades vendidas del stock.
5. La venta comienza con estado pendiente y el pago también queda pendiente. El carrito se limpia únicamente si el alta de la venta termina correctamente.
6. El cliente puede consultar el detalle de sus compras y cancelar las que continúen activas. El administrador puede cambiar el estado de venta, el estado de pago y las notas.

### Reglas de negocio actuales

- Las ventas requieren al menos un artículo y una dirección de envío válida.
- El precio de cada artículo debe ser mayor que cero y su cantidad debe ser positiva.
- Se aplica un descuento del 10 % cuando la compra contiene tres o más unidades en total.
- El costo de envío se incorpora al total; desde el checkout actual se inicializa en cero.
- Antes de descontar stock, se verifica la disponibilidad de todas las variantes de la venta para evitar descuentos parciales si falta inventario.
- La cancelación cambia el estado de la venta, pero todavía no reintegra automáticamente el stock.
- Los medios de pago disponibles son Mercado Pago, transferencia y efectivo. La selección se registra, pero no hay integración con una pasarela ni procesamiento real del pago.

## Datos de demostración

Cada ejecución carga dos cuentas por defecto, un catálogo inicial de productos y colores, tres ventas y dos artículos de carrito para cada cuenta. En cada historial hay dos ventas en curso y una entregada con el pago aprobado. Las variantes comienzan con stock de 50 unidades; las ventas de ejemplo consumen parte de ese stock al cargarse.

| Perfil | Usuario | Contraseña |
| --- | --- | --- |
| Administrador | `admin@admin.com` | `admin` |
| Cliente | `cust@cust.com` | `cust` |

Estas credenciales son solo para probar la aplicación.

## Tecnologías y diseño

- Java 21.
- Maven como descriptor del proyecto; no se requieren dependencias externas para compilar la aplicación.
- Interfaz interactiva por consola.
- Datos en memoria con repositorios basados en colecciones Java.

La organización principal del código es:

```text
src/main/java/com/calwillyfiorella/
├── exception/   Excepciones de dominio y validación
├── model/       Usuarios, productos, variantes, carrito y ventas
├── repository/  Colecciones que almacenan los datos durante la ejecución
├── service/     Autenticación y reglas de negocio
├── ui/          Menús de entrada, cliente y administrador
└── util/        Validaciones, lectura de consola y datos de demostración
```

Algunas decisiones de dominio que atraviesan estas capas:

- Un producto agrupa información general; sus variantes representan opciones vendibles con precio y stock propios.
- El carrito se consulta como un conjunto asociado a un usuario y reúne cantidades por variante.
- Una venta conserva sus artículos y los importes registrados al momento de la compra para poder mostrar el detalle posteriormente.
- El servicio de ventas valida y actualiza el inventario durante el checkout, y limita la modificación administrativa a notas y estados.

## Requisitos

- JDK 21 o superior.
- Maven es opcional para compilar: el proyecto no declara dependencias externas. También se puede compilar directamente con `javac`.

## Ejecución

Desde la raíz del repositorio, en Linux o macOS:

```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src/main/java -name '*.java')
java -cp out com.calwillyfiorella.Main
```

También se puede abrir el proyecto como proyecto Maven en un IDE compatible con Java y ejecutar `com.calwillyfiorella.Main`.

## Verificación

El repositorio todavía no incluye una suite de pruebas automatizadas. Como verificación básica, se pueden compilar todas las fuentes con el comando de `javac` indicado arriba.

## Alcance y próximos pasos

Esta versión está pensada como una demostración funcional de flujos de comercio electrónico, no como un sistema listo para producción. La información vive solo durante la ejecución y se vuelve a sembrar al reiniciar. La gestión general de usuarios, la administración de imágenes de variantes, la persistencia en una base de datos, el procesamiento de pagos y la reposición de stock cuando se cancela una venta son extensiones pendientes.

## Autoría

Proyecto personal de portfolio desarrollado por Leonel Alejandro Hidalgo.