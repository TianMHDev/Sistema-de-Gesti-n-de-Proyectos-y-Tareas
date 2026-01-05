# Project and Task Management System

This is a professional project and task management system developed with **Spring Boot 3** and a robust architecture.

## 🚀 Steps to Run the Application

### Prerequisites
- **Java 17** or higher.
- **Maven 3.8+**.
- **MySQL 8.0**.

### Option 1: Local Execution (Recommended for Dev)
1.  **Database**: Start only the database container:
    ```bash
    docker-compose up -d db
    ```
2.  **Run Application**: Use the `local` profile to connect to the Docker database:
    ```bash
    mvn spring-boot:run -Dspring-boot.run.profiles=local
    ```
3.  **Access**: 
    - **Frontend**: Open `frontend/index.html` (Change `API_URL` to `http://localhost:8086/api` in `frontend/js/app.js`).
    - **Swagger UI**: [http://localhost:8086/swagger-ui/index.html](http://localhost:8086/swagger-ui/index.html)
    - **API Docs (JSON)**: [http://localhost:8086/v3/api-docs](http://localhost:8086/v3/api-docs)

### Option 2: Full Docker Execution
1.  Run everything:
    ```bash
    docker-compose up --build
    ```
2.  The backend will be on port `8086`.

### 🛑 Stop the System
To stop all containers started with Docker:
```bash
docker-compose -p taskflow down
```
*(If you used `docker-compose up` without `-p`, use `docker-compose down`).*


---

## 🧪 Testing
The project includes unit tests for the business logic. To run them:
```bash
mvn test
```
*Note: Tests use Mockito and do not require a running database.*

---

## 🔐 Test Credentials
The system includes an automatic data initializer with the following credentials:
- **Username:** `admin`
- **Password:** `123456`

---

## 🛠️ Technical Decisions

-   **Hexagonal Architecture (Clean Architecture)**: A clear separation was implemented between the domain, use cases (application), and infrastructure. This facilitates maintenance, unit testing, and allows components (such as the database) to be changed with minimal impact.
-   **Security with JWT (JSON Web Tokens)**: Stateless authentication was chosen using Spring Security and JWT, ensuring secure communication between the frontend and the API endpoints.
-   **Soft Delete (Logical Deletion)**: Both projects and tasks use a `deleted` flag. This protects referential integrity and allows for auditing or accidental data recovery.
-   **"Pure Vanilla" Frontend**: The interface was developed with HTML5, CSS3 (with responsive and premium design), and native JavaScript. This ensures instant loading, avoids heavy dependencies, and demonstrates proficiency in web fundamentals.
-   **Relational Database (MySQL)**: MySQL was selected for its robustness and wide compatibility for persisting complex relationships between projects and tasks.
