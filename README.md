# Fluxy Demo

Aplicación de ejemplo que utiliza **spring-boot-starter-fluxy** para demostrar el motor de ejecución de flows, steps y tasks.

---

## Requisitos

| Herramienta | Versión mínima |
|-------------|---------------|
| Java (JDK)  | 25            |
| Gradle      | 8.14          |
| Docker      | 24+           |
| Docker Compose | v2+        |

---

## Inicio rápido

### 1. Publicar la librería Fluxy en Maven Local

Desde el directorio del starter:

```bash
cd ../spring-boot-starter-fluxy
./gradlew publishToMavenLocal
```

### 2. Levantar la infraestructura (PostgreSQL + LocalStack SQS)

```bash
cd fluxy-infrastructure
docker compose up -d
```

Esto levanta:
- **PostgreSQL 16** en `localhost:5432` — base de datos `fluxy_demo`, usuario `fluxy`, contraseña `fluxy`
- **LocalStack** en `localhost:4566` — cola SQS `fluxy-events` creada automáticamente al iniciar

Verificar que los servicios estén listos:

```bash
docker compose ps
```

Verificar que la cola SQS fue creada:

```bash
aws --endpoint-url=http://localhost:4566 sqs list-queues --region us-east-1
```

### 3. Ejecutar la aplicación

```bash
./gradlew bootRun
```

La aplicación arrancará en `http://localhost:8080`.

---

## Configuración de Fluxy

La aplicación está configurada con las siguientes características de Fluxy habilitadas:

| Característica | Valor | Descripción |
|---------------|-------|-------------|
| `fluxy.datasource.url` | `jdbc:postgresql://localhost:5432/fluxy_demo` | Datasource dedicado de Fluxy (EMF aislado) |
| `fluxy.jpa.hibernate.ddl-auto` | `update` | Hibernate genera/actualiza el esquema de tablas automáticamente |
| `fluxy.task.registration.auto-register` | `true` | Las clases `@Task` se persisten en BD al arrancar |
| `fluxy.task.registration.cleanup-stale.enabled` | `true` | Detecta tareas huérfanas en BD |
| `fluxy.task.registration.cleanup-stale.mode` | `WARN` | Emite warning (no detiene el arranque) |
| `fluxy.task.registration.validate-steps` | `true` | Valida versiones de tasks referenciadas en steps |
| `fluxy.eventbus.type` | `SQS` | Bus de eventos por Amazon SQS (LocalStack en local) |
| `fluxy.eventbus.sqs.queue-url` | `http://localhost:4566/000000000000/fluxy-events` | Cola SQS en LocalStack |

---

## Endpoints disponibles

Una vez arrancada, Fluxy expone automáticamente:

| Prefijo | Descripción |
|---------|-------------|
| `GET /fluxy/tasks` | Gestión de tasks |
| `GET /fluxy/steps` | Gestión de steps |
| `GET /fluxy/flows` | Gestión de flows |
| `POST /fluxy/execution/...` | Ejecución de flows, steps y tasks |
| `GET /actuator/health` | Health check |
| `GET /actuator/prometheus` | Métricas Prometheus |

Consulta el [README de spring-boot-starter-fluxy](../spring-boot-starter-fluxy/README.md) para la referencia completa de endpoints REST.

---

## Estructura del proyecto

```
fluxy-demo/
├── build.gradle                          # Dependencias y plugins
├── settings.gradle                       # Nombre del proyecto
├── gradle/wrapper/
│   └── gradle-wrapper.properties         # Gradle 8.14
├── fluxy-infrastructure/
│   ├── docker-compose.yml                # PostgreSQL + LocalStack
│   └── init-localstack.sh               # Crea la cola SQS al iniciar
├── src/
│   ├── main/
│   │   ├── java/org/fluxy/demo/
│   │   │   └── FluxyDemoApplication.java # Clase principal
│   │   └── resources/
│   │       └── application.yml           # Configuración completa
│   └── test/
│       └── java/org/fluxy/demo/
│           └── FluxyDemoApplicationTests.java
└── README.md
```

---

## Implementar tus propias Tasks

Crea una clase que extienda `FluxyTask` y anótala con `@Task`:

```java
package org.fluxy.demo.task;

import org.fluxy.core.model.task.FluxyTask;
import org.fluxy.core.model.task.TaskResult;
import org.fluxy.core.context.ExecutionContext;
import org.fluxy.spring.annotation.Task;

@Task(name = "mi-tarea", description = "Descripción de la tarea", version = 1)
public class MiTareaTask extends FluxyTask {

    public MiTareaTask() {
        this.name = "mi-tarea";
    }

    @Override
    public TaskResult execute(ExecutionContext ctx) {
        // Tu lógica aquí
        return TaskResult.SUCCESS;
    }
}
```

Al arrancar, Fluxy la detectará y registrará automáticamente en la base de datos.

