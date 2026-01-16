# ITV APIs Project - Guía de Ejecución

Este proyecto consiste en una arquitectura de microservicios para la gestión y búsqueda de estaciones ITV.

## Requisitos Previos

1.  **Java SDK**: Versión 21 instalada y configurada (`JAVA_HOME`).
2.  **PostgreSQL**: Base de datos ejecutándose en local (puerto 5432).
    *   Base de datos: `postgres`
    *   Usuario: `postgres`
    *   Contraseña: `(Configurada en application.properties)`

## Estructura del Proyecto

*   **Wrappers (Backend)**: Obtienen y normalizan datos de fuentes externas.
    *   `wrapper_gal`: Galicia (Puerto 9005)
    *   `wrapper_cat`: Catalunya (Puerto 9004)
    *   `wrapper_cv`: Comunidad Valenciana (Puerto 9003)
*   **APIs (Backend)**: Lógica de negocio y ETL.
    *   `api_carga`: Proceso de carga de datos (Puerto 9002)
    *   `api_busqueda`: Búsqueda de estaciones (Puerto 9001)
*   **Frontends (Web)**: Interfaces de usuario.
    *   `formulario_de_carga`: Panel para cargar datos (Puerto 9010)
    *   `formulario_de_busqueda`: Buscador de estaciones (Puerto 9011)

## Cómo Lanzar el Proyecto

### Opción A (Automática - Recomendada)

Simplemente ejecuta el script `launcher.bat` que se encuentra en la raíz del proyecto.
Este script abrirá ventanas terminales independientes para cada servicio en el orden correcto.

### Opción B (Manual)

Si prefieres ejecutar los servicios manualmente, abre 7 terminales y ejecuta los siguientes comandos en orden:

1.  **Wrappers:**
    ```cmd
    start "Wrapper Galicia" /D wrapper_gal mvnw.cmd spring-boot:run
    start "Wrapper Catalunya" /D wrapper_cat mvnw.cmd spring-boot:run
    start "Wrapper Valencia" /D wrapper_cv mvnw.cmd spring-boot:run
    ```

2.  **APIs:**
    ```cmd
    start "API Carga" /D api_carga mvnw.cmd spring-boot:run
    start "API Busqueda" /D api_busqueda mvnw.cmd spring-boot:run
    ```

3.  **Frontends:**
    ```cmd
    start "Formulario Carga" /D formulario_de_carga mvnw.cmd spring-boot:run
    start "Formulario Busqueda" /D formulario_de_busqueda mvnw.cmd spring-boot:run
    ```

O si prefieres ir carpeta por carpeta:

```bash
cd wrapper_gal && ./mvnw.cmd spring-boot:run
cd wrapper_cat && ./mvnw.cmd spring-boot:run
cd wrapper_cv && ./mvnw.cmd spring-boot:run
cd api_carga && ./mvnw.cmd spring-boot:run
cd api_busqueda && ./mvnw.cmd spring-boot:run
cd formulario_de_carga && ./mvnw.cmd spring-boot:run
cd formulario_de_busqueda && ./mvnw.cmd spring-boot:run
```

## Acceso

Una vez iniciados todos los servicios:

*   **Panel de Carga**: [http://localhost:9010](http://localhost:9010)
*   **Buscador**: [http://localhost:9011](http://localhost:9011)
