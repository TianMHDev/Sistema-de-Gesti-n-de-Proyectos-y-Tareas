# Sistema de Gestión de Proyectos y Tareas

Este es un sistema profesional para la gestión de proyectos y tareas, desarrollado con **Spring Boot 3** y una arquitectura robusta.

## 🚀 Pasos para Ejecutar la Aplicación

### Requisitos Previos
- **Java 17** o superior.
- **Maven 3.8+**.
- **MySQL 8.0**.

### Opción 1: Ejecución Local
1.  **Configuración de Base de Datos**: Asegúrate de tener MySQL corriendo y crea una base de datos llamada `projectdb` (o permite que el sistema la cree automáticamente).
2.  **Propiedades**: Ajusta las credenciales de conexión en `src/main/resources/application.properties` si es necesario.
3.  **Compilar y Ejecutar**:
    ```bash
    mvn clean package
    mvn spring-boot:run
    ```
4.  **Acceso**: El backend estará disponible en `http://localhost:8085`. Abre el archivo `frontend/index.html` en tu navegador.

### Opción 2: Docker (Recomendado)
1.  Asegúrate de tener Docker instalado.
2.  Ejecuta el comando:
    ```bash
    docker-compose up --build
    ```
3.  El sistema levantará automáticamente la base de datos y el backend en el puerto `8085`.

---

## 🔐 Credenciales de Prueba
El sistema incluye un inicializador de datos automático con las siguientes credenciales:
- **Usuario:** `admin`
- **Contraseña:** `123456`

---

## 🛠️ Decisiones Técnicas

-   **Arquitectura Hexagonal (Clean Architecture)**: Se implementó una clara separación entre el dominio, los casos de uso (aplicación) y la infraestructura. Esto facilita el mantenimiento, las pruebas unitarias y permite cambiar componentes (como la base de datos) con un impacto mínimo.
-   **Seguridad con JWT (JSON Web Tokens)**: Se optó por una autenticación apátrida (stateless) mediante Spring Security y JWT, garantizando una comunicación segura entre el frontend y los endpoints del API.
-   **Soft Delete (Borrado Lógico)**: Tanto proyectos como tareas utilizan una bandera `deleted`. Esto protege la integridad referencial y permite auditorías o recuperaciones de datos accidentales.
-   **Frontend "Pure Vanilla"**: Se desarrolló la interfaz con HTML5, CSS3 (con diseño responsivo y premium) y JavaScript nativo. Esto asegura una carga instantánea, evita dependencias pesadas y demuestra solvencia en los fundamentos de la web.
-   **Base de Datos Relacional (MySQL)**: Se seleccionó MySQL por su robustez y amplia compatibilidad para la persistencia de relaciones complejas entre proyectos y tareas.
