# U2 Post 1 - Analisis de Patrones de Diseno

Este proyecto implementa un sistema simple de notificaciones para demostrar el uso conjunto de **Singleton** y **Factory Method**, con una extension dinamica que refuerza el principio **OCP (Open/Closed Principle)**.

## Objetivo del proyecto

Permitir el envio de notificaciones por distintos canales (`email`, `sms`, `push`) desacoplando:

- la creacion de objetos notificador,
- el uso de dichos notificadores,
- y el registro centralizado de eventos.

## Estructura principal

- `src/main/java/com/patrones/u2/Notifier.java`: interfaz producto (`Product`).
- `src/main/java/com/patrones/u2/EmailNotifier.java`: producto concreto para correo.
- `src/main/java/com/patrones/u2/SmsNotifier.java`: producto concreto para SMS.
- `src/main/java/com/patrones/u2/PushNotifier.java`: producto concreto para Push.
- `src/main/java/com/patrones/u2/NotifierFactory.java`: fabrica con registro dinamico de creadores.
- `src/main/java/com/patrones/u2/NotificationLogger.java`: singleton global de logs.
- `src/main/java/com/patrones/u2/Main.java`: cliente de demostracion.

## Patron 1: Singleton

### Donde se aplica

En `src/main/java/com/patrones/u2/NotificationLogger.java`, usando `enum`:

```java
public enum NotificationLogger {
    INSTANCE;
    ...
}
```

### Rol dentro del sistema

- Mantiene una unica instancia global para registrar notificaciones.
- Centraliza el historial (`entries`) y su impresion (`printAll()`).
- Es consumido por todos los notificadores concretos y por `Main`.

### Evidencia en el flujo

En `src/main/java/com/patrones/u2/Main.java` se compara la misma referencia:

```java
NotificationLogger logger1 = NotificationLogger.INSTANCE;
NotificationLogger logger2 = NotificationLogger.INSTANCE;
System.out.println("Misma instancia: " + (logger1 == logger2));
```

## Patron 2: Factory Method (con registro)

### Donde se aplica

En `src/main/java/com/patrones/u2/NotifierFactory.java`:

- `REGISTRY` guarda funciones creadoras (`Supplier<Notifier>`).
- `create(String type)` devuelve el notificador solicitado.
- `register(String type, Supplier<Notifier>)` agrega nuevos tipos en runtime.

### Rol dentro del sistema

- Evita que el cliente (`Main`) use `new EmailNotifier()`, `new SmsNotifier()`, etc.
- Encapsula la logica de construccion de productos concretos.
- Reduce acoplamiento entre cliente e implementaciones concretas.

### Evidencia en el flujo

```java
Notifier email = NotifierFactory.create("email");
Notifier sms = NotifierFactory.create("sms");
Notifier push = NotifierFactory.create("push");
```

El cliente trabaja contra `Notifier` (interfaz), no contra clases concretas.

## OCP (Open/Closed Principle) demostrado

El sistema esta **abierto a extension** y **cerrado a modificacion** en su flujo principal:

- No se modifica la logica de `Main` ni la de envio existente.
- Se registra un nuevo canal (`slack`) en tiempo de ejecucion:

```java
NotifierFactory.register("slack", () -> new Notifier() {
    public String channel() { return "SLACK"; }
    public void send(String r, String m) {
        NotificationLogger.INSTANCE.log(channel(), r, m);
    }
});
```

Luego se usa como cualquier otro canal:

```java
NotifierFactory.create("slack").send("#pedidos", "Pedido #1001 procesado");
```

## Beneficios observados

- Creacion de notificadores centralizada y extensible.
- Bajo acoplamiento entre cliente y clases concretas.
- Trazabilidad unificada de eventos por medio del singleton.
- Facilidad para agregar nuevos canales sin romper codigo existente.

## Riesgos o mejoras sugeridas

- `NotificationLogger` usa `ArrayList` sin sincronizacion; en escenarios concurrentes puede requerir una estructura thread-safe.
- `NotifierFactory` usa `HashMap` mutable global; podria reforzarse con validaciones de duplicados o estrategias de concurrencia.
- Los tipos de canal en `String` pueden migrarse a `enum` o claves tipadas para evitar errores de escritura.
- Agregar pruebas unitarias para validar `create`, `register` y `printAll`.

## Ejecucion

Desde la raiz del proyecto:

```bash
mvn clean compile exec:java -Dexec.mainClass="com.patrones.u2.Main"
```

Si el plugin `exec-maven-plugin` no esta configurado, puedes compilar y ejecutar con:

```bash
mvn clean package
java -cp target/classes com.patrones.u2.Main
```

---

Este ejemplo es una base clara para estudiar como combinar patrones creacionales con principios SOLID en un caso pequeno y practico.

