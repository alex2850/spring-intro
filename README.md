# 📚 Book Marketplace API

A production-grade RESTful backend application built using **Spring Boot**, **Spring Security (JWT)**, **Spring Data JPA**, **MySQL**, **Liquibase**, and **Docker Compose**.  
This project demonstrates clean architecture, robust authentication, full CRUD functionality, and enterprise-grade development patterns.

---

## 🚀 Overview

This application provides the backend for an online book marketplace.  
It supports:

- 🔐 Secure registration & login via **JWT**
- 📘 Book management (CRUD)
- 🏷 Category management
- 🧾 Order placement and tracking
- 📄 Swagger UI for API documentation
- 🗄 Database versioning with Liquibase
- 🐳 Docker Compose for environment setup

---

## 🛠 Technologies Used

| Technology | Description |
|-----------|-------------|
| **Java 17** | Primary language |
| **Spring Boot 3** | App framework |
| **Spring Security + JWT** | Auth & authorization |
| **Spring Data JPA (Hibernate)** | DB layer |
| **MySQL 8** | Production database |
| **Liquibase** | DB schema migration |
| **MapStruct** | Entity <-> DTO mapping |
| **Lombok** | Removes boilerplate |
| **Docker Compose** | Runs MySQL + app |
| **Swagger / OpenAPI** | API documentation |
| **JUnit 5 + MockMvc** | Integration tests |

---

## 🧩 Architecture Overview

```
Controller → Service → Repository → Database
                ↓
             Security
```

✔ Controllers — expose REST API  
✔ Services — business logic  
✔ Repositories — JPA DB operations  
✔ Security — JWT + roles  
✔ Entities/DTOs — clean data structures

---

## 🔐 Authentication Flow (JWT)

1. **POST /auth/registration** — register new user  
2. **POST /auth/login** — receive JWT token  
3. Include token for protected endpoints:

```
Authorization: Bearer <your_token>
```

Roles:

- `ROLE_USER`
- `ROLE_ADMIN`

---

## 📂 Controllers Overview

### 👤 Auth Controller (`/auth`)

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/auth/registration` | Public | Register new user |
| POST | `/auth/login` | Public | Login → get JWT |

---

### 📘 Book Controller (`/books`)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | `/books` | USER | Get all books (pagination) |
| GET | `/books/{id}` | USER | Get book by id |
| POST | `/books` | ADMIN | Create new book |
| PATCH | `/books/{id}` | ADMIN | Update book |
| DELETE | `/books/{id}` | ADMIN | Delete book |

---

### 🏷 Category Controller (`/categories`)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | `/categories` | USER | Get all categories |
| GET | `/categories/{id}` | USER | Get category by id |
| POST | `/categories` | ADMIN | Create category |
| PUT | `/categories/{id}` | ADMIN | Update |
| DELETE | `/categories/{id}` | ADMIN | Soft delete |

---

## 📦 Local URLs

```
http://localhost:8080/books
http://localhost:8080/auth/login
http://localhost:8080/swagger-ui/index.html
```

---

## 🐳 Running with Docker Compose

### 1️⃣ Create `.env` in project root:

```
MYSQLDB_USER=root
MYSQLDB_ROOT_PASSWORD=ALEX1998
MYSQLDB_DATABASE=testdb
MYSQLDB_LOCAL_PORT=3308
MYSQLDB_DOCKER_PORT=3306

SPRING_LOCAL_PORT=8081
SPRING_DOCKER_PORT=8080

DEBUG_PORT=5005
```

---

### 2️⃣ Start containers

```
docker compose up --build
```

---

### 3️⃣ Access application

Swagger UI:
```
http://localhost:8080/swagger-ui/index.html
```

Books API:
```
http://localhost:8080/books
```

Login:
```
http://localhost:8080/auth/login
```

---

## 📝 Running Locally (no Docker)

Build JAR:

```
mvn clean package
```

Run:

```
mvn spring-boot:run
```

---

## 📌 Sample Requests

### Registration

```
POST /auth/registration
{
  "email": "user@mail.com",
  "password": "12345",
  "repeatPassword": "12345",
  "firstName": "John",
  "lastName": "Doe"
}
```

### Login

```
POST /auth/login
{
  "email": "user@mail.com",
  "password": "12345"
}
```

### Create Book (Admin)

```
POST /books
Authorization: Bearer <token>

{
  "title": "New Book",
  "author": "Author",
  "isbn": "1234567890123",
  "price": 25.50,
  "description": "Good book",
  "categoryIds": [1]
}
```

---

## 🧠 Obstacles & Solutions

| Problem | Fix |
|--------|------|
| Liquibase migrations failing | Split changelogs & correct order |
| JWT filters blocking access | Correct filter chain order |
| MySQL container slow startup | Added health checks + depends_on |
| Integration tests failing | Used real POST for entity creation |

---

## 📬 Contact

**Author:** Oleksandr Dombrovskyi  
**Email:** dombrovskiy2850@mail.com  
