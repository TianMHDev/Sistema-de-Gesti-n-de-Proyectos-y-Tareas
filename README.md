# Project and Task Management System

This is a professional project and task management system developed with **Spring Boot 3** and a robust architecture.

## 🚀 Steps to Run the Application

### Prerequisites
- **Java 17** or higher.
- **Maven 3.8+**.
- **MySQL 8.0**.

### Option 1: Local Execution
1.  **Database Configuration**: Ensure MySQL is running and create a database named `projectdb` (or allow the system to create it automatically).
2.  **Properties**: Adjust the connection credentials in `src/main/resources/application.properties` if necessary.
3.  **Build and Run**:
    ```bash
    mvn clean package
    mvn spring-boot:run
    ```
4.  **Access**: The backend will be available at `http://localhost:8085`. Open the `frontend/index.html` file in your browser.

### Option 2: Docker (Recommended)
1.  Ensure you have Docker installed.
2.  Run the command:
    ```bash
    docker-compose up --build
    ```
3.  The system will automatically start the database and the backend on port `8085`.

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
