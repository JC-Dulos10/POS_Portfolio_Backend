# POS Portfolio Backend

Spring Boot REST API backend with security and Docker support.

## Features
- Spring Boot 3
- PostgreSQL database
- Spring Security with JWT
- Docker / Docker Compose setup

## Run locally with Docker

1. Build and start services:
   ```bash
   docker compose up --build
   ```

2. The API will be available at `http://localhost:8080`.

## Sample endpoints
- `POST /api/auth/signin`
- `POST /api/auth/signup`
- `GET /api/products`

## Switch to MySQL

To use MySQL instead of PostgreSQL, update `docker-compose.yml` and `src/main/resources/application.yml`:
- image: `mysql:8.0`
- JDBC URL: `jdbc:mysql://db:3306/posdb`
- add `spring.datasource.driver-class-name: com.mysql.cj.jdbc.Driver`
- add MySQL connector dependency in `pom.xml`
